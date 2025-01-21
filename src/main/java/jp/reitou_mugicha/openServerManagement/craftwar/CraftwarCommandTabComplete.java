package jp.reitou_mugicha.openServerManagement.craftwar;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public class CraftwarCommandTabComplete implements TabCompleter
{
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        List<String> completions = new ArrayList<>();

        if (args.length == 1)
        {
            completions.add("start");
            completions.add("stop");
            completions.add("reroll");
            completions.add("setobjective");
        } else if (args.length == 2 && args[0].equals("setobjective"))
        {
            completions.addAll(
                    Arrays.stream(Material.values())
                            .filter(Material::isItem)
                            .map(Material::toString)
                            .map(String::toLowerCase)
                            .toList()
            );
        }

        List<String> result = new ArrayList<>();
        for (String completion : completions) {
            if (completion.startsWith(args[args.length - 1])) {
                result.add(completion);
            }
        }

        return result;
    }
}
