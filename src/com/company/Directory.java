package com.company;

import java.io.Serializable;
import java.util.ArrayList;

public class    Directory implements Serializable
{
    public Directory()
    {
        name = "";
        directoryPath = "";
        files = new ArrayList<>();
        subDirectories = new ArrayList<>();
    }
    public Directory(String name, String directoryPath)
    {
        this.name = name;
        this.directoryPath = directoryPath;
        files = new ArrayList<>();
        subDirectories = new ArrayList<>();
    }

    public String name;
    public String directoryPath;
    public ArrayList<file> files;
    public ArrayList<Directory> subDirectories;

    public static Directory directorySearch(Directory directory, String[] pathSplit, int depth, int level)
    {
        if(depth == 0)
        {
            return directory;
        }
        for(int i = 0 ; i<directory.subDirectories.size() ; i++)
        {
            if(directory.subDirectories.get(i).name.equals(pathSplit[level]))
            {
                return directorySearch(directory.subDirectories.get(i), pathSplit, depth - 1, level + 1);
            }
        }
        return null;
    }

    public static void createFolder(String path)
    {
        String[] pathSplit = path.split("/");
        if(pathSplit.length < 2 || !pathSplit[0].equals("root"))
        {
            System.err.println("Path Error");
            return;
        }
        int depth = pathSplit.length - 2;
        Directory directory = directorySearch(virtualFileSystem.root, pathSplit, depth, 1);
        if(directory == null)           //Wrong path
        {
            System.err.println("No such directory");
            return;
        }
        //authorization
        boolean authorized = virtualFileSystem.directoryAuthorize(directory, "create");
        if(!authorized)
        {
            System.err.println("Unauthorized user");
            return;
        }
        for(Directory d : directory.subDirectories)
        {
            if(d.name.equals(pathSplit[pathSplit.length-1]))
            {
                System.err.println("There is a directory with the same name");
                return;
            }
        }

        Directory d = new Directory();
        //add the direcotory
        d.name = pathSplit[pathSplit.length-1];
        d.directoryPath = path;
        d.files = new ArrayList<>();
        d.subDirectories = new ArrayList<>();
        directory.subDirectories.add(d);
        //add this folder to capabilities Arraylist, initially with admin capabilities
        ArrayList<pair<String, String>> usersCapabilities = new ArrayList<>();
        usersCapabilities.add(new pair<>("admin", "11"));
        virtualFileSystem.capabilities.add(new pair<>(d.name, usersCapabilities));
        System.out.println("Directory created successfully");
    }

    public static void deleteFolder(String path)
    {
        String[] pathSplit = path.split("/");
        if(pathSplit.length < 2 || !pathSplit[0].equals("root"))
        {
            System.err.println("Path Error");
            return;
        }
        int depth = pathSplit.length - 2;
        Directory directory = directorySearch(virtualFileSystem.root, pathSplit, depth, 1);
        if(directory == null)
        {
            System.err.println("No such directory");
            return;
        }
        //authorization
        boolean authorized = virtualFileSystem.directoryAuthorize(directory, "delete");
        if(!authorized)
        {
            System.err.println("Unauthorized user");
            return;
        }
        for(Directory d : directory.subDirectories)
        {
            if(d.name.equals(pathSplit[pathSplit.length-1]))
            {
                for(int i = 0 ; i < d.files.size() ; i++)           //delete all files under this directory
                {
                    file.deleteFile(path + "/" + d.files.get(i).name);
                }
                directory.subDirectories.remove(d);
                for(int i = 0 ; i<virtualFileSystem.capabilities.size() ; i++)          //delete the directory from capabilities ArrayList
                {
                    pair<String, ArrayList<pair<String, String>>> entry = virtualFileSystem.capabilities.get(i);
                    if(d.name.equals(entry.first))
                    {
                        virtualFileSystem.capabilities.remove(i);
                    }
                }
                System.out.println("Directory deleted successfully");
                return;
            }
        }
        System.err.println("no such folder");
    }
}
