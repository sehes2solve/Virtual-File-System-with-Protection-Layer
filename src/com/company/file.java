package com.company;

import java.io.Serializable;

public class file implements Serializable
{
    public String name;
    public String method;
    public String filePath;
    public int[] allocatedBlocks;

    public static void createFile(String path, int size, String method)
    {
        String[] pathSplit = path.split("/");
        if(pathSplit.length < 2 || !pathSplit[0].equals("root"))
        {
            System.err.println("Path Error");
            return;
        }
        int depth = pathSplit.length - 2;
        Directory directory = Directory.directorySearch(virtualFileSystem.root, pathSplit, depth, 1);
        if(directory == null)
        {
            System.err.println("No such file or directory");
            return;
        }
        //authorization
        boolean authorized = virtualFileSystem.directoryAuthorize(directory, "create");
        if(!authorized)
        {
            System.err.println("Unauthorized user");
            return;
        }
        for(file f : directory.files)
        {
            if(f.name.equals(pathSplit[pathSplit.length-1]))
            {
                System.err.println("There is a file with the same name");
                return;
            }
        }

        file f = new file();
        if(method.equals("contiguous"))
        {
            f.allocatedBlocks = virtualFileSystem.contiguousAallocate(size);
            f.method = "contiguous";
        }
        else if(method.equals("indexed"))
        {
            f.allocatedBlocks = virtualFileSystem.indexedAallocate(size);
            f.method = "indexed";
        }
        if(f.allocatedBlocks == null)
        {
            System.err.println("no enough contiguous space");
            return;
        }
        //add the file
        f.name = pathSplit[pathSplit.length-1];
        f.filePath = path;
        directory.files.add(f);
        if(method.equals("contiguous"))
            virtualFileSystem.allocatedBlocksForFiles.add(f);
        if(method.equals("indexed"))
            virtualFileSystem.indexBlock.add(f);
        System.out.println("File created successfully");
    }
    public static void deleteFile(String path)
    {
        String[] pathSplit = path.split("/");
        if(pathSplit.length < 2 || !pathSplit[0].equals("root"))
        {
            System.err.println("Path Error");
            return;
        }
        int depth = pathSplit.length - 2;
        Directory directory = Directory.directorySearch(virtualFileSystem.root, pathSplit, depth, 1);
        if(directory == null)
        {
            System.err.println("No such file or directory");
            return;
        }
        //authorization
        boolean authorized = virtualFileSystem.directoryAuthorize(directory, "delete");
        if(!authorized)
        {
            System.err.println("Unauthorized user");
            return;
        }
        for(file f : directory.files)
        {
            if(f.name.equals(pathSplit[pathSplit.length-1]))
            {
                for(int i = f.allocatedBlocks[0] ; i < f.allocatedBlocks[0] + f.allocatedBlocks.length ; i++)
                    virtualFileSystem.freeSpaceManager[i] = 0;
                directory.files.remove(f);
                if(f.method.equals("contiguous"))
                    virtualFileSystem.allocatedBlocksForFiles.remove(f);
                else if(f.method.equals("indexed"))
                    virtualFileSystem.indexBlock.remove(f);
                System.out.println("File deleted successfully");
                return;
            }
        }
        System.err.println("no such file");
    }
}