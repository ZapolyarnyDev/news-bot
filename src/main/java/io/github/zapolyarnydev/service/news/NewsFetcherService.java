package io.github.zapolyarnydev.service.news;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import io.github.zapolyarnydev.model.News;
import io.github.zapolyarnydev.repository.SentNewsRepository;
import io.github.zapolyarnydev.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

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
            Document document = builder.parse(url.openStream());

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(document);

            return feed.getEntries().stream()
                    .map(entry -> convertToNews(entry, category))
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


    private News convertToNews(SyndEntry entry, String category) {
        String description = entry.getDescription() == null ? " " : entry.getDescription().getValue();
        return new News(entry.getTitle(),
                description,
                entry.getLink(),
                category);
    }
}
