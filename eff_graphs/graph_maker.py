import random

def make_vertices(count):
	v = []
	for i in range(ord('A'), ord('Z')+1):
		for j in range(ord('A'), ord('Z')+1):
			if len(v) >= count:
				break
			v.append("" + chr(i) + "" + chr(j))
	return v



def make_graph(file, vertices, connectivity):
	f = open(file, 'w')
	f.write(str(len(vertices)))
	random.seed(0)
	for i in vertices:
		for j in vertices:
			if(random.random() < connectivity):
				f.write("\n" + i + " " + j)

	f.close()

make_graph(
	"effGraph",
	make_vertices(500),
	0.1
)


