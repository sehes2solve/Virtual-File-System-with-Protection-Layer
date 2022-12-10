package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class virtualFileSystem {

    private static final int diskSize = 10000;
    static Directory root = new Directory("root", "root");
    static int freeSpaceManager[] = new int[diskSize];        //initially all places are empty
    static ArrayList<file> allocatedBlocksForFiles = new ArrayList<>();
    static ArrayList<file> indexBlock = new ArrayList<>();
    static String currentUser = new String();
    static ArrayList<User> users = new ArrayList<>();
    static ArrayList<pair<String, ArrayList<pair<String, String>>>> capabilities = new ArrayList<>();
  //  static ArrayList<String> indexedBlocksForFiles = new ArrayList<>();


    public static void login(String username, String password)
    {
        for(User user : users)
        {
            if(user.username.equals(username) && user.password.equals(password))
            {
                currentUser = username;
                System.out.println("Login successful");
                return;
            }
        }
        System.err.println("Login failed : Invalid username or password");
    }
    public static void tellUser()
    {
        System.out.println(currentUser);
    }
    public static void grant(String username, String path, String capability)
    {
        if(!currentUser.equals("admin"))
        {
            System.err.println("Unauthorized action : only admin can use this function");
        }
        String[] pathSplit = path.split("/");
        if(!pathSplit[0].equals("root"))
        {
            System.err.println("Path Error");
            return;
        }
        boolean found = false;
        for(User user : users)      //make sure that the given user is in system
        {
            if(user.username.equals(username)) {
                found = true;
                break;
            }
        }
        if(!found)
        {
            System.err.println("No such user");
            return;
        }
        //int depth = -1;
        //if(pathSplit.length == 1)
           int depth = pathSplit.length - 1;        //if path = root
        //else if(pathSplit.length >= 2) depth = pathSplit.length - 2;
        Directory directory = Directory.directorySearch(virtualFileSystem.root, pathSplit, depth, 1);
        if(directory == null)
        {
            System.err.println("No such directory");
            return;
        }
        for(pair<String, ArrayList<pair<String, String>>> entry : capabilities)
        {
            if(entry.first.equals(directory.name))
            {
                for(pair<String, String> usersCapabilities : entry.second)      //search users capabilities to that folder
                {
                    if(usersCapabilities.first.equals(username)) {             //if I found that user already in capabilities, so I will modify his capabilities with the new one
                        usersCapabilities.second = capability;
                        System.out.println("Granted successfully");
                        return;
                    }
                }
                entry.second.add(new pair<>(username, capability));             //if the user capabilities added the first time for this folder
                System.out.println("Granted successfully");
                return;
            }
        }
    }
    public static void displayAllUsers()
    {
        for(User user : users)
        {
            System.out.println(user.username);
        }
    }
    public static void displayAllCapabilities()
    {
        for(pair<String, ArrayList<pair<String, String>>> entry : capabilities)
        {
            System.out.print(entry.first + " : ");
            for(pair<String, String> usersCapabilities : entry.second)
            {
                System.out.print(usersCapabilities.first + " " + usersCapabilities.second + ", ");
            }
            System.out.println("");
        }
    }
    public static int[] indexedAallocate(int size)
    {
        if(size > diskSize)
        {
            System.err.println("no free space");
            return null;
        }

        int[] allocated = new int[size]; int x = 0;
        int indexedSpace = 0;
        int i = 0;

        while(i < diskSize && indexedSpace < size)
        {
            if(freeSpaceManager[i] == 1) {i++; continue;}
            allocated[x++] = i;
            indexedSpace++;
            i++;
        }

        if(indexedSpace < size)
        {
            System.err.println("no free space");
            return null;
        }

        for(int j = 0 ; j < size ; j++)
        {
            freeSpaceManager[allocated[j]] = 1;
        }
        return allocated;
    }
    public static int[] contiguousAallocate(int size)
    {
        if(size > diskSize)
        {
            System.err.println("no free space");
            return null;
        }
        int contiguousSpace = 0;
        int head = 0;
        int i;
        for(i = 0 ; i <diskSize ; )                  //point to the first free block
            if(freeSpaceManager[i] == 1) i++;
            else
            {
                head = i;
                break;
            }

        while(i < diskSize && freeSpaceManager[i] == 0 && contiguousSpace < size)
        {
            contiguousSpace++;
            i++;
            while(i < diskSize && freeSpaceManager[i] == 1)
            {
                i++;
                head = i;
                contiguousSpace = 0;
            }
        }

        if(contiguousSpace < size)
        {
            System.err.println("no free space");
            return null;
        }
        int[] allocated = new int[size]; int x = 0;
        for(int j = head ; j < head+size ; j++)
        {
            freeSpaceManager[j] = 1;
            allocated[x++] = j;
        }
        return allocated;
    }
    public static void displayDiskStatus()
    {
        int emptySpace = 0;
        int allocatedSpace = 0;
        ArrayList<Integer> emptyBlocks = new ArrayList<>();
        ArrayList<Integer> allocatedBlocks = new ArrayList<>();
        for(int i = 0 ; i < diskSize ; i++)
        {
            if(freeSpaceManager[i] == 0)
            {
                emptySpace++;
                emptyBlocks.add(i);
            }
            else
            {
                allocatedSpace++;
                allocatedBlocks.add(i);
            }
        }
        System.out.println("Allocated Space = " + allocatedSpace);
        for(int i : allocatedBlocks) System.out.print(i + ",");
        System.out.println("");
        System.out.println("Empty Space = " + emptySpace);
        for(int i : emptyBlocks) System.out.print(i + ",");
        System.out.println("");
    }

    public static void displayDiskStructure(Directory directory, String directoryOutputSpace, String fileOutputSpace)
    {
        System.out.println(directoryOutputSpace + directory.name);
        if(directory.files.size() > 0)
        {
            for(file f : directory.files)
                System.out.println(fileOutputSpace + f.name);
        }
        if(directory.subDirectories.size() > 0)
        {
            for(Directory d : directory.subDirectories)
                displayDiskStructure(d, directoryOutputSpace + "   ", fileOutputSpace + "   ");
        }
    }
   /* public static void traverse(Directory directory)
    {
        if(directory.files.size() > 0)
        {
            for(file f : directory.files)
                if(f.method.equals("contiguous"))
                    allocatedBlocksForFiles.add(f.name + " " + f.allocatedBlocks[0] + " " + f.allocatedBlocks.length);
              *//*  else if(f.method.equals("indexed"))
                    indexedBlocksForFiles.add()*//*
        }
        if(directory.subDirectories.size() > 0)
        {
            for(Directory d : directory.subDirectories)
                traverse(d);
        }
    }*/

    public static void saveVFS()
    {
        try (
                OutputStream ss = new FileOutputStream("VFS.ser");
                ObjectOutput output = new ObjectOutputStream(ss);
        )
        {
            output.writeObject(root);                       //to save Files and Folders Directory Structure.
            output.writeObject(freeSpaceManager);           //to save free space manager
            output.writeObject(allocatedBlocksForFiles);    //to save The allocated blocks for files
            output.writeObject(indexBlock);                  //to save The allocated blocks for files
            System.out.println("Virtual File System saved successfully");
        }
        catch (IOException e) {
            logger.log(Level.SEVERE, "Cannot perform output.", e);
        }
    }
    public static void saveUsers()
    {
        try (
                OutputStream ss = new FileOutputStream("user.ser");
                ObjectOutput output = new ObjectOutputStream(ss);
        )
        {
            output.writeObject(users);
            System.out.println("users saved successfully");
        }
        catch (IOException e) {
            logger.log(Level.SEVERE, "Cannot perform output.", e);
        }
    }
    public static void saveCapabilities()
    {
        try (
                OutputStream ss = new FileOutputStream("capabilities.ser");
                ObjectOutput output = new ObjectOutputStream(ss);
        )
        {
            output.writeObject(capabilities);
            System.out.println("capabilities saved successfully");
        }
        catch (IOException e) {
            logger.log(Level.SEVERE, "Cannot perform output.", e);
        }
    }
    public static void saveSystem()
    {
        saveVFS();
        saveUsers();
        saveCapabilities();
    }
    public static void loadVFS() {
        try (
                InputStream ss = new FileInputStream("VFS.ser");
                ObjectInput input = new ObjectInputStream(ss);
        ) {
            //deserialize
            root = (Directory) input.readObject();
            freeSpaceManager = (int[]) input.readObject();
            allocatedBlocksForFiles = (ArrayList<file>) input.readObject();
            indexBlock = (ArrayList<file>) input.readObject();
            System.out.println("Virtual File System loaded successfully");
        } catch (ClassNotFoundException ex) {
            root = new Directory("root", "root");
            System.out.println("Virtual File System loaded successfully1");
        } catch (EOFException e) {
            logger.log(Level.SEVERE, "Cannot perform input.", e);
        } catch (Exception ex) {
            root = new Directory("root", "root");
            System.out.println("Virtual File System loaded successfully2");
            //   ex.printStackTrace();
        }
    }
    public static void loadUsers() {
        try (
                InputStream ss = new FileInputStream("user.ser");
                ObjectInput input = new ObjectInputStream(ss);
        ) {
            //deserialize
            users = (ArrayList<User>) input.readObject();
            System.out.println("Users loaded successfully");
        } catch (ClassNotFoundException ex) {
            users.add(new User("admin", "admin"));
            System.out.println("Users loaded successfully1");
        } catch (EOFException e) {
            logger.log(Level.SEVERE, "Cannot perform input.", e);
        } catch (Exception ex) {
            users.add(new User("admin", "admin"));
            System.out.println("Users loaded successfully2");
            //   ex.printStackTrace();
        }
    }
    public static void loadCapabilities() {
        try (
                InputStream ss = new FileInputStream("capabilities.ser");
                ObjectInput input = new ObjectInputStream(ss);
        ) {
            //deserialize
            capabilities = (ArrayList<pair<String, ArrayList<pair<String, String>>>>) input.readObject();
            System.out.println("Capabilities loaded successfully");
        } catch (ClassNotFoundException ex) {
            ArrayList<pair<String, String>> usersCapabilities = new ArrayList<>();
            usersCapabilities.add(new pair<>("admin", "11"));
            capabilities.add(new pair("root", usersCapabilities));
            System.out.println("Capabilities loaded successfully1");
        } catch (EOFException e) {
            logger.log(Level.SEVERE, "Cannot perform input.", e);
        } catch (Exception ex) {
            ArrayList<pair<String, String>> usersCapabilities = new ArrayList<>();
            usersCapabilities.add(new pair<>("admin", "11"));
            capabilities.add(new pair("root", usersCapabilities));
            System.out.println("Capabilities loaded successfully2");
            //   ex.printStackTrace();
        }
    }
    public static void loadSystem()
    {
        loadVFS();
        loadUsers();
        loadCapabilities();
        currentUser = "admin";
    }
    public static boolean authorize(String directory, String action)
    {
        for(pair<String, ArrayList<pair<String, String>>> entry : capabilities)
        {
            if(directory.equals(entry.first))
            {
                for(pair<String, String> usersCapabilities : entry.second)
                {
                    if(currentUser.equals(usersCapabilities.first))
                    {
                        if(action.equals("create") && (usersCapabilities.second.equals("11") || usersCapabilities.second.equals("10")))
                            return true;
                        else if(action.equals("delete") && (usersCapabilities.second.equals("11") || usersCapabilities.second.equals("01")))
                            return true;
                    }
                }
            }
        }
        return false;
    }
    public static boolean directoryAuthorize(Directory directory, String action)
    {
        String[] pathSplit = directory.directoryPath.split("/");
        for(String dir : pathSplit)
        {
            if(authorize(dir, action)) return true;
        }
        return false;
    }
    private static final Logger logger =
            Logger.getLogger(virtualFileSystem.class.getPackage().getName());
}
