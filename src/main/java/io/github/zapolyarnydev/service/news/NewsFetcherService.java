package io.github.zapolyarnydev.service.news;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import io.github.zapolyarnydev.model.News;
import io.github.zapolyarnydev.repository.SentNewsRepository;
import io.github.zapolyarnydev.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.jdom2.Element;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import javax.xml.parsers.*;
import java.net.URL;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsFetcherService {

    private final MessageService messageService;
    private final SentNewsRepository sentNewsRepository;

    public List<News> fetchByCategory(String category) {
        try {
            var url = new URL(messageService.getMessage("news-url." + category));

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setNamespaceAware(false);
            factory.setValidating(false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document document = builder.parse(url.openStream());

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(document);

            return feed.getEntries().stream()
                    .map(entry -> convertToNews(entry, category))
                    .filter(news -> news.imageUrl() != null)
                    .sorted((news1, news2) -> {
                        boolean hasDescription1 = news1.description() != null;
                        boolean hasDescription2 = news2.description() != null;

                        if (hasDescription1 && !hasDescription2) {
                            return -1;
                        } else if (!hasDescription1 && hasDescription2) {
                            return 1;
                        } else {
                            return 0;
                        }
                    })
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<News> getNewUnsetNews(Long chatId, List<News> newsList){
        return newsList.stream()
                .filter(news -> !isSent(chatId, news.url()))
                .toList();
    }

    private boolean isSent(Long chatId, String newsUrl){
        String newsId = chatId + "_" + DigestUtils.md5Hex(newsUrl);
        return sentNewsRepository.existsById(newsId);
    }

    private String extractImageUrl(SyndEntry entry) {
        if (entry.getEnclosures() != null && !entry.getEnclosures().isEmpty()) {
            return entry.getEnclosures().get(0).getUrl();
        }

        try {
            if (entry.getForeignMarkup() != null) {
                for (Element element : entry.getForeignMarkup()) {
                    if ("media:content".equals(element.getName())
                            && element.getAttribute("url") != null
                            && element.getAttributeValue("medium").equals("image")) {
                        return element.getAttributeValue("url");
                    }
                }
            }
        } catch (Exception ignored) {}

        if (entry.getDescription() != null) {
            String html = entry.getDescription().getValue();
            try {
                Document doc = Jsoup.parse(html);
                org.jsoup.nodes.Element img = doc.select("img").first();
                if (img != null) {
                    return img.attr("src");
                }
            } catch (Exception ignored) {}
        }

        return null;
    }

    private News convertToNews(SyndEntry entry, String category) {
        String description = entry.getDescription() == null ? " " : entry.getDescription().getValue();
        return new News(entry.getTitle(),
                description,
                entry.getLink(),
                category,
                extractImageUrl(entry));
    }
}
