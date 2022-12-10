package com.company;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable
{
    String username;
    String password;
    User()
    {
        username = null;
        password = null;
    }
    User(String username, String password)
    {
        this.username = username;
        this.password = password;
    }
    public static void createUser(String username, String password)
    {
        if(!virtualFileSystem.currentUser.equals("admin"))
        {
            System.err.println("Unauthorized action : only admin can create a user");
            return;
        }
        for(User user : virtualFileSystem.users)
        {
            if(user.username.equals(username))
            {
                System.err.println("There is a user exist with the same username!");
                return;
            }
        }
        virtualFileSystem.users.add(new User(username, password));
        System.out.println("User created successfully");
    }
    public static void deleteUserCapabilities(String username)
    {
        for(pair<String, ArrayList<pair<String, String>>> entry : virtualFileSystem.capabilities)       //loop on all directories in capabilities Arraylist
        {
            for(int i = 0 ; i<entry.second.size() ; i++)                                                //search for user capabilities for this directory
            {
                pair<String, String> usersCapabilities = entry.second.get(i);
                if(usersCapabilities.first.equals(username))
                {
                    entry.second.remove(i);
                    break;
                }
            }
        }
    }
    public static void deleteUser(String username)
    {
        if(!virtualFileSystem.currentUser.equals("admin"))
        {
            System.err.println("Unauthorized action : only admin can delete a user");
            return;
        }

        deleteUserCapabilities(username);               //delete the user from capabilities Arraylist

        for(User user : virtualFileSystem.users)
        {
            if(user.username.equals(username))
            {
                boolean temp = virtualFileSystem.users.remove(user);
                if(temp) System.out.println("User deleted successfully");
                else System.err.println("Deleting failed!");
                return;
            }
        }
        System.err.println("There is no such user with this username");
    }
}
