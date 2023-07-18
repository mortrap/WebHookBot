package org.natasha;

import com.annimon.tgbotsmodule.BotHandler;
import com.annimon.tgbotsmodule.commands.CommandRegistry;
import com.annimon.tgbotsmodule.commands.SimpleCommand;
import com.annimon.tgbotsmodule.commands.authority.For;
import com.annimon.tgbotsmodule.commands.authority.SimpleAuthority;
import com.annimon.tgbotsmodule.services.YamlConfigLoaderService;

import org.jetbrains.annotations.NotNull;
import org.natasha.commands.GuessNumberGame;
import org.natasha.commands.TinkoffPiapiCommands;
import org.natasha.commands.YouTubeThumbnail;
import org.natasha.config.HookBotConfig;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public class HookBotHandler extends BotHandler {

    private final HookBotConfig botConfig;
    private final CommandRegistry<For> commands;

    public HookBotHandler() {
        super("");
        final var configLoader = new YamlConfigLoaderService();
        botConfig = configLoader.loadResource("/testbot.yaml", HookBotConfig.class);
        final var authority = new SimpleAuthority( botConfig.getAdminId());
        commands = new CommandRegistry<>( this.getBotUsername(),authority);
        commands.register(new SimpleCommand("/start", ctx -> {
            ctx.reply("Hi, " + ctx.user().getFirstName() + "\n" +
                            "This bot is an example for tgbots-module library.\n" +
                            "https://github.com/aNNiMON/tgbots-module/\n\n" +
                            "Available commands:\n" +
                            " - /game — start a guess number game\n" +
                            " - /sand - start a sandbox mode tinkoff invest api, most have tokens  in .env file\n" +
                            " - /curr - iso names list tradable currencies for current command\n" +
                            " - /orderbook + name -to get orderbook for concrete instrument finded with uid/figi  edit in a code\n" +
                            " - /show + name - to get concrete tradable currency information\n" +
                            "Also, you can send me a link to YouTube video and I'll send you a video thumbnail as a photo.")
                    .disableWebPagePreview()
                    .callAsync(ctx.sender);
        }));
        commands.register(new YouTubeThumbnail());
        commands.registerBundle(new GuessNumberGame());
        commands.registerBundle(new TinkoffPiapiCommands());
    }

    @Override
    protected BotApiMethod<?> onUpdate(@NotNull Update update) {
        if (commands.handleUpdate(this, update)) {
            return null;
        }
//        final var msg = update.getMessage();
//        if (msg != null && msg.hasText()) {
//            System.out.println(msg.getChatId());
//            Methods.sendMessage(msg.getChatId(), msg.getText().toUpperCase(Locale.ROOT))
//                    .callAsync(this);
//            log.info(msg.getText());
//        }
        return null;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
}



