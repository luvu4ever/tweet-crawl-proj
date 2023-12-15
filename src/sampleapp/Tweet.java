package sampleapp;

public class Tweet {
    String account;
    String timeStamp;
    String tweetText;
    int reply;

    public String getAccount() {
        return account;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getTweetText() {
        return tweetText;
    }

    public int getReply() {
        return reply;
    }

    public int getRetweet() {
        return retweet;
    }

    public int getLike() {
        return like;
    }

    int retweet;
    int like;
    public Tweet(String account, String timeStamp, String tweetText, int reply, int retweet, int like){
        this.account = account;
        this.timeStamp = timeStamp;
        this.tweetText = tweetText;
        this.reply = reply;
        this.retweet = retweet;
        this.like = like;
    }
    public static Tweet createTweet(String account, String timeStamp, String tweetText, int reply, int retweet, int like){
        return new Tweet(account, timeStamp, tweetText, reply, retweet, like);
    }

}
