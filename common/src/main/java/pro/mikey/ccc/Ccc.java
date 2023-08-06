package pro.mikey.ccc;

import pro.mikey.ccc.commands.Command;
import pro.mikey.ccc.commands.HomeCommand;

import java.util.LinkedList;
import java.util.List;

public class Ccc {
    public static LinkedList<Command> commands = new LinkedList<>();

    public static void init() {
        commands.addAll(List.of(
           new HomeCommand()
        ));
    }

}
