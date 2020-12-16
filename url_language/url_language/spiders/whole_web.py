from url_language.items import UrlWholeWebItem
from scrapy.spiders import CrawlSpider, Rule
from scrapy.linkextractors import LinkExtractor
import re

class WholeWebSpider(CrawlSpider):
  
  name = 'whole_web'

  rules = (
        Rule(LinkExtractor(), callback='parse', follow=True),
  )

  def __init__(self, filename, *a, **kw):
    super(WholeWebSpider, self).__init__(*a, **kw)
    if filename:
      with open(filename, 'r') as f:
        self.start_urls = f.readlines()
        
  def parse(self, response):

    item = UrlWholeWebItem()
    item['url'] = response.url
    # extracting basic body
    raw_body = '\n'.join(response.xpath('//text()').extract())
    clean_body = re.sub(' +', ' ', re.sub(r'[^\w]', ' ', raw_body))
    if len(clean_body) < 1000:  
      item['body'] = clean_body
    else:
      half = int((len(raw_body)/2))
      item['body'] = clean_body[half - 500 : half + 500]

    return item

    