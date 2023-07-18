package org.natasha.commands;

import com.annimon.tgbotsmodule.commands.*;
import com.annimon.tgbotsmodule.commands.authority.For;
import com.annimon.tgbotsmodule.commands.context.MessageContext;
import io.github.cdimascio.dotenv.Dotenv;

import org.jetbrains.annotations.NotNull;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.tinkoff.piapi.contract.v1.Currency;
import ru.tinkoff.piapi.contract.v1.Order;
import ru.tinkoff.piapi.core.InvestApi;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static ru.tinkoff.piapi.core.utils.MapperUtils.quotationToBigDecimal;

public class TinkoffPiapiCommands implements CommandBundle<For> {
    private final Dotenv dotenv = Dotenv.configure()
            .directory("src/main/resources")
            .filename(".env") // instead of '.env', use 'env'
            .load();
    InvestApi sandboxApi = InvestApi.createSandbox(getEnvTok("TOKEN_SAND"));
    InvestApi realApi = InvestApi.create(getEnvTok("TOKEN_REAL"));


    String getEnvTok(String s) {
        assert dotenv != null;
        return dotenv.get(s);
    }

    @Override
    public void register(@NotNull CommandRegistry<For> commands) {
        commands.register(new SimpleCommand("/sand", this::sandboxModeJoin));
        commands.register(new SimpleCommand("/real", this::realModeJoin));
        commands.register(new SimpleCommand("/curr", this::currencyInformation));
        commands.register(new SimpleCommand("/order-book", this::orderBook));
        //commands.register(new SimpleCommand("/put" + " " + "inputText", this::putInMap));
        commands.register(new SimpleCommand("/show", this::getCurrencyByPrompt));
    }

    private void orderBook(MessageContext messageContext) {
        var text = messageContext.argument(0, "en");
        var getInstrumentByIsoName = sandboxApi.getInstrumentsService().getTradableCurrenciesSync().stream()
                .filter(c -> c.getIsoCurrencyName().startsWith(text))
                .toList();
        var getCurrencyUid = getInstrumentByIsoName.stream().map(Currency::getUid).toList();
        var depth = 3;
        var orderBook = sandboxApi.getMarketDataService().getOrderBookSync(getCurrencyUid.get(0), depth);
        var asks = orderBook.getAsksList();
        var bids = orderBook.getBidsList();
        var lastPrice = quotationToBigDecimal(orderBook.getLastPrice());
        var closePrice = quotationToBigDecimal(orderBook.getClosePrice());
        messageContext.reply()
                .setText("Стакан для инструмента: " + orderBook.getDescriptorForType().getFullName() +
                        "Количество предложений на продажу:" + asks.size() + "\n" +
                        "Цена последней сделки: " + lastPrice + "\n" +
                        "Цена закрытия: " + closePrice)
                .call(messageContext.sender);

//        log.info(
//                "получен стакан по инструменту {}, глубина стакана: {}, количество предложений на покупку: {}, количество " +
//                        "предложений на продажу: {}, цена последней сделки: {}, цена закрытия: {}",
//                "a22a1263-8e1b-4546-a1aa-416463f104d3", depth, bids.size(), asks.size(), lastPrice, closePrice);
//
        for (Order bid : bids) {
            var price = quotationToBigDecimal(bid.getPrice());
            var quantity = bid.getQuantity();
            messageContext.reply()
                    .setText("To Buy lots:" + "\n" +
                            "Quantity Lots: " + quantity + "\n" +
                            "Price: " + price)
                    .call(messageContext.sender);
        }

        for (Order ask : asks) {
            var price = quotationToBigDecimal(ask.getPrice());
            var quantity = ask.getQuantity();
            messageContext.reply()
                    .setText("To Sell lots: " + "\n" +
                            "Quantity lots: " + quantity + "\n" +
                            "Price: " + price)
                    .call(messageContext.sender);
        }
    }

    private void currencyInformation(MessageContext messageContext) {
        if (!messageContext.message().isUserMessage()) return;
        var currList = sandboxApi.getInstrumentsService().getTradableCurrenciesSync();
        // var currencyByUid = sandboxApi.getInstrumentsService().getCurrencyByUidSync("a22a1263-8e1b-4546-a1aa-416463f104d3");
        messageContext.reply()
                .setText(currList.stream().map(Currency::getIsoCurrencyName)
                        .toList().toString())
                .call(messageContext.sender);

//                .setText("Finded currency by iso currency name: " + currList.stream()
//                        .filter(c -> c.getIsoCurrencyName().toLowerCase().startsWith("usd"))
//                        .collect(Collectors.toSet()).stream()
//                        .map(Currency::getUid)
//                        .toList())
    }

    private void getCurrencyByPrompt(MessageContext messageContext) {
        var text = messageContext.argument(0, "en");
        var curSet = sandboxApi.getInstrumentsService().getTradableCurrenciesSync().stream()
                .filter(c -> c.getTicker().toLowerCase().startsWith(text))
                .toList();
        messageContext.reply()
                .setText(curSet.toString())
                .call(messageContext.sender);

    }

    //TODO methods for bot/tinkoff piapi
    private void sandboxModeJoin(MessageContext messageContext) {
        if (!messageContext.message().isUserMessage()) return;
        var accounts = sandboxApi.getUserService().getAccountsSync();
        var mainAcc = accounts.get(0);
//        var msg = messageContext.reply()
//                .setText(String.valueOf("Accounts: " + accounts))
//                .call(messageContext.sender);
        messageContext.reply()
                .setText("Main account info: " + mainAcc.getAllFields())
                .call(messageContext.sender);
    }

    private void realModeJoin(MessageContext messageContext) {

    }

    public List<List<InlineKeyboardButton>> keyboard(long userId) {
        return Stream.of(IntStream.range(0, 5), IntStream.range(5, 10))
                .map(stream -> stream
                        .mapToObj(i -> InlineKeyboardButton.builder()
                                .text(Integer.toString(i))
                                .callbackData(String.format("guess:%d %d", userId, i))
                                .build())
                )
                .map(stream -> stream.collect(Collectors.toList()))
                .collect(Collectors.toList());
    }


}

