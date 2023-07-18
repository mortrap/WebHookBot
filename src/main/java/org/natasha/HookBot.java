package org.natasha;

import com.annimon.tgbotsmodule.BotHandler;
import com.annimon.tgbotsmodule.BotModule;
import com.annimon.tgbotsmodule.Runner;
import com.annimon.tgbotsmodule.beans.Config;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HookBot implements BotModule {

    public static void main(String[] args) {
        Runner.run(List.of(new HookBot()));
    }

    @Override
    public @NotNull BotHandler botHandler(@NotNull Config config) {
        return new HookBotHandler();
    }


}