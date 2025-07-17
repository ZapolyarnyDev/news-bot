<h1 align="center">NewsBot</h1> 

<p align="center">
  <a href="https://github.com/zapolyarnydev/news-bot/actions/workflows/run-tests.yml">
    <img src="https://img.shields.io/github/actions/workflow/status/zapolyarnydev/news-bot/run-tests.yml?style=flat&label=Unit%20tests" alt="Unit test status"/>
  </a>
</p>

## О боте
Реализация телеграм бота, который парсит новости из rss-агрегаторов, даёт возможность
выбрать категорию и периодичность отправки новостей, с возможностью отписаться/подписаться заново в любой момент.<br>
Бот умеет фильтровать новости по наличию описания и ссылки на новость, парсит и прикрепляет соответствующее к новости фото.<br>
Имеет интуитивно понятный интерфейс, с возможностью удалять новости после прочтения

## Используемые технологии
- Java 21;
- Spring Boot;
- Docker;
- Gradle;
- Git;
- JUnit 5 & Mockito
- Telegram bots.

## Пример работы
<p align="center">
  <img src="images/bot-settings.jpg" alt="Главное меню бота" width="400"/>
  <img src="images/bot-news-example.jpg" alt="Пример новости" width="450"/>
</p>

## Лицензия

Copyright (c) 2025 ZapolyarnyDev

Проект распространяется под лицензией MIT — см. файл [LICENSE](LICENSE) для подробностей.
