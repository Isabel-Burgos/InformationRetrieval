# Define here the models for your scraped items
#
# See documentation in:
# https://docs.scrapy.org/en/latest/topics/items.html

import scrapy


class UrlWholeWebItem(scrapy.Item):
  
  url = scrapy.Field()
  body= scrapy.Field()

class UrlLanguageItem(scrapy.Item):
  
  url = scrapy.Field()
