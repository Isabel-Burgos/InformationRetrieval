from url_language.items import UrlLanguageItem
from url_language.naivebayes import NaiveBayes
from scrapy.spiders import CrawlSpider, Rule
from scrapy.linkextractors import LinkExtractor

class FocusedCrawlerSpider(CrawlSpider):
  
  name = "focused_crawler"
  
  rules = (
      Rule(LinkExtractor(), callback='parse_outlink'),
  )
  
  def __init__(self, filename, *a, **kw):
    
    super(FocusedCrawlerSpider, self).__init__(*a, **kw)
    
    self.NB = NaiveBayes()
    self.NB.train()       
    
    if filename:
      with open(filename, 'r') as f:
        self.start_urls = f.readlines()
 
  def parse_outlink(self, response):
    
    dutch = self.NB.predict([response.url])
    
    # if the outlink is classified as dutch
    if dutch[0] == '1':
    
      item = UrlLanguageItem()
    
      item['url'] = response.url
      
      yield item
    
      return response.follow(response.url, self.parse_outlink)