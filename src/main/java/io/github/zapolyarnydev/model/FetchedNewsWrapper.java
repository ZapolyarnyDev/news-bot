package io.github.zapolyarnydev.model;

import java.util.List;

public record FetchedNewsWrapper(List<News> news, String category) {
}
