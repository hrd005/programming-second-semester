//package stepanoff.denis.lab5.client.cmd;
//
///**
// * Implementation of 'save' command.
// */
//public class SaveCommand extends Command {
//
//    {
//        this.name = "save";
//        this.description = ": save collection to file.";
//
//        this.action = (String... a) -> {
//
////            if (!this.collection.isUnsaved()) {
////                ConsoleWriter.println("Collection contains no unsaved changes.", ConsoleWriter.Color.GREEN);
////                return;
////            }
////
////            FileWriter writer = FileIO.getWriter();
////            File saved = this.collection.getFile();
////            try {
////                writer.write(this.collection, saved);
////            } catch (FileWritingException e) {
////                ConsoleWriter.println(e.getMessage(), ConsoleWriter.Color.RED);
////                saved = new File("temp" + Instant.now().toEpochMilli());
////                ConsoleWriter.println("Trying to save to " + saved.getPath());
////
////                try {
////                    writer.write(this.collection, saved);
////                } catch (FileWritingException e1) {
////                    ConsoleWriter.println(e1.getMessage(), ConsoleWriter.Color.RED);
////                    return;
////                }
////            }
////
////            try {
////                this.collection.reload(this.collection.getFile());
////            } catch (Exception e) {
////                ConsoleWriter.println("Could not update current state: " + e.getMessage(), ConsoleWriter.Color.RED);
////            }
////
////            ConsoleWriter.println("Collection saved.", ConsoleWriter.Color.GREEN);
//        };
//    }
//}
