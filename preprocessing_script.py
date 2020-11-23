import csv
import math

files = ['dmoz_domain_category.csv', 'parsed-new.csv', 'parsed-subdomain.csv']

dutch_webs = []
german_webs = []
spanish_webs = []
us_webs = []
uk_webs = []
polish_webs = []
french_webs = []
swedish_webs = []
norwegian_webs = []

for file in files:

  with open(file, encoding="utf8") as csv_file:
    
    reader = csv.reader(csv_file)
    
    for row in reader:
      
      cat = row[1].lower()
      
      if not row[0].startswith('www.') and not row[0].startswith('.'):
        url = 'www.' + row[0]
      elif row[0].startswith('.'):
        url = 'www' + row[0]
      else:
        url = row[0]
      
      if 'nederlands' in cat.split('/'):
        dutch_webs.append(url)  
      
      elif 'deutsch' in cat.split('/'):
        german_webs.append(url)  
        
      elif 'español' in cat.split('/'):
        spanish_webs.append(url)  
      
      elif 'united_states' in cat.split('/'):
        us_webs.append(url)  
        
      elif 'united_kingdom' in cat.split('/'):
        uk_webs.append(url)  
      
      elif 'polski' in cat.split('/'):
        polish_webs.append(url)  
        
      elif 'français' in cat.split('/'):
        french_webs.append(url)  
      
      elif 'svenska' in cat.split('/'):
        swedish_webs.append(url)  
        
      elif 'norsk' in cat.split('/'):
        norwegian_webs.append(url)  

dutch_urls = list(set(dutch_webs))
german_urls = list(set(german_webs))
spanish_urls = list(set(spanish_webs))
us_urls = list(set(us_webs))
uk_urls = list(set(uk_webs))
polish_urls = list(set(polish_webs))
french_urls = list(set(french_webs))
swedish_urls = list(set(swedish_webs))
norwegian_urls = list(set(norwegian_webs))

# we crop the length of the list to be a cleaner number 

subset_length = math.floor(len(dutch_urls)/8)

dutch_urls = dutch_urls[0:(subset_length*8)]

# now we only get 16000 entries of each language

non_dutch_urls = []
non_dutch_urls.extend(german_urls[0:subset_length])
non_dutch_urls.extend(spanish_urls[0:subset_length])
non_dutch_urls.extend(us_urls[0:subset_length])
non_dutch_urls.extend(uk_urls[0:subset_length])
non_dutch_urls.extend(polish_urls[0:subset_length])
non_dutch_urls.extend(french_urls[0:subset_length])
non_dutch_urls.extend(swedish_urls[0:subset_length])
non_dutch_urls.extend(norwegian_urls[0:subset_length])

with open('train_data.txt', 'w') as txt_file:
  
  for url in dutch_urls:
    txt_file.write("1\t{}\n".format(url))
    
  for url in non_dutch_urls:
    txt_file.write("0\t{}\n".format(url))
  

  