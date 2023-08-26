package me.beemo.commands.pollCommand;

import java.util.ArrayList;

public class Choice {

    private String id;
    private String name;
    public ArrayList<String> usersVoted = new ArrayList<>();
    private int votes = 0;

    public Choice(String choiceId, String choice) {
        id = choiceId;
        name = choice;
    }

    int getVotes() {
        return this.votes;
    }

    public String getName(){
        return this.name;
    }

    public String getId(){
        return this.id;
    }

    public void addVote(String userId) {
        usersVoted.add(userId);
        this.votes = getVotes() + 1;
    }

    public void removeVote(String userId) {
        usersVoted.remove(userId);
        this.votes = getVotes() - 1;
    }

    public boolean hasVoted(String userId) {
        return usersVoted.contains(userId);
    }
}
