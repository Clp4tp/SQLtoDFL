import codecs
import random, string
def randomword(length):
   return ''.join(random.choice(string.lowercase) for i in range(length))

if __name__ == '__main__':
	filename = raw_input("Give a filename: ")
	f = codecs.open(filename, 'w', 'utf-8')
	rows =  int(raw_input("Give rows: "))
	for i in range(0,rows): 	
		f.write("%s|%s|%s|%s|\n" %(i, randomword(7), randomword(10), i+40)) 
	print "end"
		
