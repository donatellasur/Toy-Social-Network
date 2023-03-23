package service;

import domain.Friendship;
import domain.User;
import repository.database.FriendshipDBRepo;
import repository.database.UserDBRepo;

public class Network {
//    private FriendshipFileRepository repoFriendship;
//    private UserFileRepo repoUser;
    private FriendshipDBRepo repoFrDB;
    private UserDBRepo repoDB;

    public Network(FriendshipDBRepo repoFrDB, UserDBRepo repoDB) {
        this.repoFrDB = repoFrDB;
//        this.repoUser = repoUser;
        this.repoDB = repoDB;
    }

    public Long getNewIdUser()
    {
        Long id = 0L;
        for(User u:repoDB.findAll())
            id = u.getId();
        return id+1;
    }

    public void addUser(User user)
    {
        user.setId(getNewIdUser());
        repoDB.save(user);
    }

    public void updateUser(User user){
        repoDB.update(user);
    }

    public void updateFriendship(Friendship friendship){
        repoFrDB.update(friendship);
    }

    public Iterable<User> getUsers()
    {
        return repoDB.findAll();
    }

    public Iterable<Friendship> getFriendships()
    {
        return repoFrDB.findAll();
    }

    public User deleteUser(Long id)
    {
        try{
            User t = repoDB.findOne(id);
            if(t == null)
                throw new IllegalArgumentException("The user does not exist!");
            for(Friendship fr: repoFrDB.findAll())
                if(fr.getIdUser2().equals(id) || fr.getIdUser1().equals(id))
                    repoFrDB.delete(fr.getId());
            User e = repoDB.delete(id);
            return e;
        }
        catch (IllegalArgumentException e){System.out.println("Invalid user!");}

        return null;
    }


    public Long getNewIdFriendship()
    {
        Long id = 0L;
        for(Friendship u:repoFrDB.findAll())
            id = u.getId();
        return id+1;
    }

    public void addFriendship(Friendship friendship)
    {
        for(Friendship f:getFriendships())
            if(f.getIdUser1().equals(friendship.getIdUser1()) && f.getIdUser2().equals(friendship.getIdUser2()))
                throw new IllegalArgumentException("The friendship already exist!");
        else if(repoDB.findOne(friendship.getIdUser1()) == null || repoDB.findOne(friendship.getIdUser2()) == null)
            throw new IllegalArgumentException("The user does not exist");
        if(friendship.getIdUser2().equals(friendship.getIdUser1()))
            throw new IllegalArgumentException("The ids can not be the same!");
        friendship.setId(getNewIdFriendship());
        repoFrDB.save(friendship);
    }

    public Friendship deleteFriendship(Long id)
    {
        if(repoFrDB.findOne(id) == null)
            throw new IllegalArgumentException("The id is invalid!");
        return repoFrDB.delete(id);
    }

}