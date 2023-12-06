import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;


public class NFT {
    public static void main(String[] args) {
        String url = "https://books.toscrape.com";
        
        try{
            Document document = Jsoup.connect(url).get();
            Elements books = document.select(".product_pod");

            for(Element bk:books){
                String title = bk.select("h3 > a").attr("title");
                String price = bk.select(".product_price .price_color").text();
                System.out.println(title + " " + price);
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
