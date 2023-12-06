package sampleapp;

public class containstest {
    public static void main(String[] args) {
        String text = "Very thankful shoutout to \n" +
                "@pranksyNFT\n" +
                " when he gave me the heads up that led to me being able to mint 10 bored apes in may. \uD83D\uDE4F\n" +
                "@BoredApeYC\n" +
                " Not selling.";
        String keyword = "boredapeyc";
        if(text.toLowerCase().contains(keyword.toLowerCase()))
            System.out.println("true");
        else
            System.out.println("false");
    }
}
