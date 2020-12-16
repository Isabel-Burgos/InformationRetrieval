from langdetect import detect
from tqdm import tqdm
import csv

filename = 'meh.csv'

with open(filename, newline='', encoding='utf8') as csvfile:
  
  spamreader = csv.reader(csvfile, delimiter=',')
  
  language_dict = {}
  
  for entry in tqdm(spamreader):
    if len(language_dict) == 50000:
      break;
    try:
      language_dict[entry[1]] = detect(entry[0])
    except:
      pass
     
  csvfile.close()
    
result_file = filename.split('.')[0] + '_result.txt' 

with open(result_file, 'w', encoding='utf8') as result_file:
  
  all_languages = set(language_dict.values())
  lang_count = {}  
  
  for lang in all_languages:
    lang_count[lang] = list(language_dict.values()).count(lang)
    
  dutch_webs = lang_count['nl']
  
  non_dutch_webs = 0
  
  non_dutch_lang = list(all_languages)
  non_dutch_lang.remove('nl')
  
  for lang in non_dutch_lang:
    non_dutch_webs += lang_count[lang]
  
  harvest_rate = dutch_webs/non_dutch_webs
  
  print("Result of the language detection of {}".format(filename), file=result_file)
  print(lang_count, file=result_file)
  
  print("Number of dutch webs: {}".format(dutch_webs), file=result_file)
  print("Number of non-dutch webs: {}".format(non_dutch_webs), file=result_file)
  print("Harvest rate is: {}".format(harvest_rate), file=result_file)
  
  
  print("\nThe links classified as dutch (including dialects) are:\n", file=result_file)
  for key in language_dict:
    if language_dict[key] != 'nl':
      print(key, file=result_file)

  result_file.close()