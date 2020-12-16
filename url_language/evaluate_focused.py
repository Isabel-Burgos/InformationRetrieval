# -*- coding: utf-8 -*-
"""
Created on Sat Dec 12 11:09:34 2020

@author: Javi
"""

from langdetect import detect
from tqdm import tqdm
import csv
import re
import requests

filename = 'random_focused.csv'

with open(filename, newline='', encoding='utf8') as csvfile:
  
  spamreader = csv.reader(csvfile, delimiter=',') 
  
  next(spamreader, None) #skip header
  
  language_dict = {}
  unopened_urls = []
  
  
  for cnt, entry in enumerate(tqdm(spamreader)):
    
    try:

      url_content = requests.get(entry[0], timeout=5).text
      clean_body = re.sub(' +', ' ', re.sub(r'[^\w]', ' ', url_content))
      if len(clean_body) > 1000:  
        half = int((len(clean_body)/2))
      clean_body = clean_body[half - 500 : half + 500]
      language_dict[entry[0]] = detect(clean_body)
      
    except:
      
      unopened_urls.append(entry[0])
     
  csvfile.close()
  
dutch_urls = []  

for key in language_dict:
  if language_dict[key] == 'nl':
    dutch_urls.append(language_dict[key])
    

txtfile_name = filename.split(".")[0] + "_result.txt"
  
with open(txtfile_name, 'w', encoding='utf8') as out_file:
  
  print("{} opened urls, from which {} are dutch. ({}%) {} unprocessed urls.\n".format(len(language_dict),
                                                                                   len(dutch_urls),
                                                                                   len(dutch_urls)/len(language_dict)*100,
                                                                                   len(unopened_urls)
                                                                                   ), file=out_file)
  for unopen in unopened_urls:
    print(unopen, file=out_file)
  out_file.close()