package ui;

public class PrintHelp {

    public PrintHelp() {}

    public static String print(String where) {
        return switch (where) {
            case "login" -> """
                    register <Username> <Password> <Email> - to create an account
                    login <Username> <Password> - to play chess
                    quit - exit application
                    help - view possible commands""";
            case "authenticated" -> """
                    create <Name> - create a new game
                    list - list games
                    play <Id> [White|Black] - join a game
                    observe <Id> - view a game
                    logout - log out user
                    quit - exit application
                    help - view possible commands""";
            case "in game" -> """
                    quit - exit current game\
                    
                    help - view possible commands\
                    
                    redraw - redraw chess board\
                    
                    move <Start> <End> <Promotion> - move a piece from the start point to the end point, \
                    positions should be of the form a1 or e6, Promotion should be a piece type or 'null'\
                    
                    resign - forfeit and end game\
                    
                    highlight <Position> - highlights the legal moves of the selected position,\
                    positions should be of the form a1 or e6""";
            case "post resign" -> "quit - exit current game" +
                    "\nhelp - view possible commands";
            case "observing" -> """
                    quit - exit current game\
                    
                    redraw - redraw chess board\
                    
                    highlight <Position> - highlights the legal moves of the selected position,\
                    positions should be of the form a1 or e6\
                    
                    help - view possible commands""";
            case null, default -> "position not recognized";
        };
    }
}
