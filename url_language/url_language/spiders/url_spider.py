import scrapy
from items import UrlLanguageItem


class UrlSpider(scrapy.Spider):
  
  name = 'url_language'
  # allowed_domains = ['www.tripadvisor.com']
  start_urls = ['https://www.tripadvisor.com/Attractions-g186338-Activities-a_allAttractions.true-London_England.html']

  def parse(self, response):
    
    item = UrlLanguageItem()
    
    item['title'] = response.xpath('fghdfghdf').extract()
    
    
    
    
    pass