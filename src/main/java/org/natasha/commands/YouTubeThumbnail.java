package org.natasha.commands;

import com.annimon.tgbotsmodule.commands.RegexCommand;
import com.annimon.tgbotsmodule.commands.authority.For;
import com.annimon.tgbotsmodule.commands.context.RegexMessageContext;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.EnumSet;
import java.util.regex.Pattern;

public class YouTubeThumbnail implements RegexCommand {
    @Override
    public Pattern pattern() {
        return Pattern.compile("https?://(?:www\\\\.)?youtu(?:\\\\.be/|be.com/watch\\\\?v=)([^#&?\\\\s]+)");
    }

    @Override
    public EnumSet<For> authority() {
        return For.all();
    }

    @Override
    public void accept(@NotNull RegexMessageContext ctx) {
        var url = "https://img.youtube.com/vi/" + ctx.group(1) + "/hqdefault.jpg";
        ctx.replyWithPhoto()
                .setFile(new InputFile(url))
                .setCaption(url)
                .callAsync(ctx.sender);
    }
    }

