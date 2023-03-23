package service;

import domain.Friendship;
import domain.Request;
import domain.User;
import repository.database.FriendshipDBRepo;
import repository.database.RequestDBRepo;
import repository.database.UserDBRepo;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NetworkDB {
    private final FriendshipDBRepo repoFriendshipDB;
    private final UserDBRepo repoUserDB;
    private final RequestDBRepo repoRequestDB;

    public NetworkDB(FriendshipDBRepo repoFriendship, UserDBRepo repoUser, RequestDBRepo repoRequestDB) {
        this.repoFriendshipDB = repoFriendship;
        this.repoUserDB = repoUser;
        this.repoRequestDB = repoRequestDB;
    }

    public Long getNewIdUser() {
        Long id = 0L;
        for (User u : repoUserDB.findAll())
            if(u.getId() > id)
                id = u.getId();
        return id + 1;
    }

    public void addUser(User user) {
        user.setId(getNewIdUser());
        repoUserDB.save(user);
    }

    public List<User> getUsers() {
        return (List<User>) repoUserDB.findAll();
    }
    public User getUserById(Long id){
        return repoUserDB.findOne(id);
    }

    public Iterable<Friendship> getFriendships() {
        return repoFriendshipDB.findAll();
    }
    public List<Friendship> getFriendsFor(Long id) {
        List<Friendship> friendships = new ArrayList<>();
        for (Friendship f : repoFriendshipDB.findAll())
            if(f.getIdUser1().equals(id) || f.getIdUser2().equals(id))
                friendships.add(f);
        return friendships;
    }

    public List<Request> getRequestsFor(Long id) {
        List<Request> requests = new ArrayList<>();
        for (Request r : repoRequestDB.findAll())
            if(r.getIdUser2().equals(id))
                requests.add(r);
        return requests;
    }

    public List<Request> getRequests() {
        return (List<Request>) repoRequestDB.findAll();
    }

    public User deleteUser(Long id) {
        try {
            User t = repoUserDB.findOne(id);
            if (t == null)
                throw new IllegalArgumentException("The user does not exist!");
            for (Friendship fr : repoFriendshipDB.findAll())
                if (fr.getIdUser2().equals(id) || fr.getIdUser1().equals(id))
                    repoFriendshipDB.delete(fr.getId());
            User e = repoUserDB.delete(id);
            return e;
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid user!");
        }

        return null;
    }
    public User updateUser(User new_user){
        return repoUserDB.update(new_user);
    }

    public Long getNewIdFriendship() {
        Long id = 0L;
        for (Friendship u : repoFriendshipDB.findAll())
            if(u.getId() > id)
                id = u.getId();
        return id + 1;
    }
    public Long getNewIdRequest() {
        Long id = 0L;
        for (Request u : repoRequestDB.findAll())
            if(u.getId() > id)
                id = u.getId();
        return id + 1;
    }

    public void sendRequest(Long id1, Long id2) {
        Request r = new Request(id1, id2, "pending");
        Long id = getNewIdRequest();
        r.setId(id);
        repoRequestDB.save(r);
    }
    public void acceptRequest(Long id1, Long id2) {
        Request r = new Request(id1, id2, "accepted");
        Long id = getNewIdRequest();
        r.setId(id);
        repoRequestDB.save(r);
        LocalDateTime date = LocalDateTime.now();
        Friendship f = new Friendship(id1, id2, date);
        Long idf = getNewIdFriendship();
        f.setId(idf);
        repoFriendshipDB.save(f);
    }

    public void deleteRequest(Long id, Long id1) {
        for (Request r : repoRequestDB.findAll())
            if (r.getIdUser1().equals(id) && r.getIdUser2().equals(id1))
                repoRequestDB.delete(r.getId());
    }

    public void addFriendship(Friendship friendship) {
        for (Friendship f : getFriendships())
            if (f.getIdUser1().equals(friendship.getIdUser1()) && f.getIdUser2().equals(friendship.getIdUser2()))
                throw new IllegalArgumentException("The friendship already exist!");
            else if (repoUserDB.findOne(friendship.getIdUser1()) == null || repoUserDB.findOne(friendship.getIdUser2()) == null)
                throw new IllegalArgumentException("The user does not exist");
        if (friendship.getIdUser2().equals(friendship.getIdUser1()))
            throw new IllegalArgumentException("The ids can not be the same!");
        friendship.setId(getNewIdFriendship());
        repoFriendshipDB.save(friendship);
    }

    public Friendship deleteFriendship(Long id) {
        if (repoFriendshipDB.findOne(id) == null)
            throw new IllegalArgumentException("The id is invalid!");
        return repoFriendshipDB.delete(id);
    }
    public Friendship updateFriendship(Friendship friendship){
        return repoFriendshipDB.update(friendship);
    }

}