package com.gmail.berndivader.mythicdenizenaddon.cmds;

import com.denizenscript.denizencore.exceptions.CommandExecutionException;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsException;
import com.denizenscript.denizencore.objects.Argument;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.gmail.berndivader.mythicdenizenaddon.MythicMobsAddon;
import com.gmail.berndivader.mythicdenizenaddon.StaticStrings;
import com.gmail.berndivader.mythicdenizenaddon.obj.dMythicItem;

import java.util.Iterator;
import java.util.regex.Pattern;

public class GetMythicItems extends AbstractCommand {

    @Override
    public void parseArgs(ScriptEntry entry) throws InvalidArgumentsException {
        for (Argument arg : entry.getProcessedArgs()) {
            if (!entry.hasObject(StaticStrings.FILTER) && arg.matchesPrefix(StaticStrings.FILTER)) {
                entry.addObject(StaticStrings.FILTER, arg.asElement());
            } else if (!entry.hasObject(StaticStrings.STRICT) && arg.matchesPrefix(StaticStrings.STRICT)) {
                entry.addObject(StaticStrings.STRICT, arg.asElement());
            }
        }

        entry.defaultObject(StaticStrings.FILTER, new ElementTag(""));
        entry.defaultObject(StaticStrings.STRICT, new ElementTag(false));
    }

    @Override
    public void execute(ScriptEntry entry) throws CommandExecutionException {
        Pattern p = Pattern.compile(entry.getElement(StaticStrings.FILTER).asString());

        if (!entry.getElement(StaticStrings.STRICT).asBoolean()) {
            Iterator<String> it = MythicMobsAddon.mythicMobsInstance.getItemManager().getItemNames().iterator();
            ListTag list = new ListTag();
            while (it.hasNext()) {
                String s1 = it.next();
                if (p.matcher(s1).find()) {
                    list.add(new dMythicItem(s1).identify());
                }
            }
            entry.addObject("mythicitems", list);
        } else {
            dMythicItem mi = new dMythicItem(p.pattern());
            if (mi.isPresent()) {
                entry.addObject("mythicitem", mi);
            } else {
                throw new CommandExecutionException("Failed to create " + dMythicItem.class.getSimpleName());
            }
        }
    }
}
