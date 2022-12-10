package com.company;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        virtualFileSystem.loadSystem();

        System.out.println("\nAvailable commands : ");
        System.out.println("tellUser");
        System.out.println("login username password");
        System.out.println("grant username path capabilities");
        System.out.println("createUser username password");
        System.out.println("deleteUser userName");
        System.out.println("createFile fullpath/fileName fileSize method        (method : contiguous or indexed)");
        System.out.println("createFolder fullpath/folderName");
        System.out.println("deleteFile fullpath/fileName");
        System.out.println("deleteFolder fullpath/folderName");
        System.out.println("displayDiskStatus");
        System.out.println("displayDiskStructure");
        System.out.println("displayAllocatedBlocks");
        System.out.println("displayAllUsers");
        System.out.println("displayAllCapabilities");
        System.out.println("Quit\n");

        Scanner in = new Scanner(System.in);
        while (in.hasNextLine()) {
            String command = in.nextLine();
            command = command.toLowerCase();
            if (command.equals("exit")) return;
            String[] commandSplit = command.split(" ");

            if(commandSplit[0].equals(("telluser")))
            {
                if(commandSplit.length != 1)
                {
                    System.err.println("Wrong number of arguments");
                    continue;
                }
                virtualFileSystem.tellUser();
            }
            else if(commandSplit[0].equals(("login")))
            {
                if(commandSplit.length != 3)
                {
                    System.err.println("Wrong number of arguments");
                    continue;
                }
                virtualFileSystem.login(commandSplit[1], commandSplit[2]);
            }
            else if(commandSplit[0].equals(("grant")))
            {
                if(commandSplit.length != 4)
                {
                    System.err.println("Wrong number of arguments");
                    continue;
                }
                virtualFileSystem.grant(commandSplit[1], commandSplit[2], commandSplit[3]);
            }
            else if(commandSplit[0].equals(("createuser")))
            {
                if(commandSplit.length != 3)
                {
                    System.err.println("Wrong number of arguments");
                    continue;
                }
                User.createUser(commandSplit[1], commandSplit[2]);
            }
            else if(commandSplit[0].equals(("deleteuser")))
            {
                if(commandSplit.length != 2)
                {
                    System.err.println("Wrong number of arguments");
                    continue;
                }
                User.deleteUser(commandSplit[1]);
            }
            else if(commandSplit[0].equals(("displayallusers")))
            {
                if(commandSplit.length != 1)
                {
                    System.err.println("Wrong number of arguments");
                    continue;
                }
                virtualFileSystem.displayAllUsers();
            }
            else if(commandSplit[0].equals(("displayallcapabilities")))
            {
                if(commandSplit.length != 1)
                {
                    System.err.println("Wrong number of arguments");
                    continue;
                }
                virtualFileSystem.displayAllCapabilities();
            }
            else if (commandSplit[0].equals("createfile")) {
                if (commandSplit.length != 4) {
                    System.err.println("Wrong number of arguments");
                    continue;
                }
                file.createFile(commandSplit[1], Integer.parseInt(commandSplit[2]), commandSplit[3]);
            } else if (commandSplit[0].equals("createfolder")) {
                if (commandSplit.length != 2) {
                    System.err.println("Wrong number of arguments");
                    continue;
                }
                Directory.createFolder(commandSplit[1]);
            } else if (commandSplit[0].equals("deletefile")) {
                if (commandSplit.length != 2) {
                    System.err.println("Wrong number of arguments");
                    continue;
                }
                file.deleteFile(commandSplit[1]);
            } else if (commandSplit[0].equals("deletefolder")) {
                if (commandSplit.length != 2) {
                    System.err.println("Wrong number of arguments");
                    continue;
                }
                Directory.deleteFolder(commandSplit[1]);
            } else if (commandSplit[0].equals("displaydiskstatus")) {
                if (commandSplit.length != 1) {
                    System.err.println("Wrong number of arguments");
                    continue;
                }
                virtualFileSystem.displayDiskStatus();
            } else if (commandSplit[0].equals("displaydiskstructure")) {
                if (commandSplit.length != 1) {
                    System.err.println("Wrong number of arguments");
                    continue;
                }
                virtualFileSystem.displayDiskStructure(virtualFileSystem.root, "", "   ");
            }
            else if(commandSplit[0].equals("displayallocatedblocks"))
            {
                if (commandSplit.length != 1) {
                    System.err.println("Wrong number of arguments");
                    continue;
                }
                System.out.println("Allocated Blocks for contiguous files : ");
                for(file f : virtualFileSystem.allocatedBlocksForFiles)
                {
                    System.out.println(f.name + " " + f.allocatedBlocks[0] + " " + f.allocatedBlocks.length);
                }
                System.out.println("Allocated Blocks for Indexed files");
                for(int j = 0 ; j < virtualFileSystem.indexBlock.size() ; j++)
                {
                    file f = virtualFileSystem.indexBlock.get(j);
                    System.out.println(f.name + " " + j);
                    for(int i : f.allocatedBlocks) System.out.print(i + " ");
                    System.out.println("");
                }
            } else if (commandSplit[0].equals("quit")) {
                if (commandSplit.length != 1) {
                    System.err.println("Wrong number of arguments");
                    continue;
                }
                virtualFileSystem.saveSystem();
                return;
            } else {
                System.err.println("Invalid command!");
                continue;
            }
        }
    }
}
