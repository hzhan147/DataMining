
import random
import math

def performKMeans(K, dataset):
    initialSamples = random.sample(dataset,K)
    clusters = dict([(i,[]) for i in initialSamples])
    centroids = initialSamples

    loop = 1
    while(loop):
        for data in dataset:
            centroid = getSmallestCentroid(data,centroids)
            clusters[centroid].append(data)

        newCentroids = []
        for i, clusterSet in enumerate(clusters):
            cxTotal = 0.0
            cyTotal = 0.0
            total = 0
            for cluster in clusters[clusterSet]:
                total = total+1
                cSet = cluster.split(',')
                cxTotal = cxTotal + float(cSet[0])
                cyTotal = cyTotal + float(cSet[1])
            cxMean = cxTotal/total
            cyMean = cyTotal/total
            newCentroids.append(str(cxMean)+','+str(cyMean))
        if(centroids == newCentroids):
            loop = 0
        else:
            centroids = newCentroids
            clusters = dict([(i,[]) for i in newCentroids])
    return clusters

def getSmallestCentroid(X,Centroids):
    xSets = X.split(',')
    x = float(xSets[0])
    y = float(xSets[1])

    distArrays = {}
    for centroid in Centroids:
        cSets = centroid.split(',')
        cx = float(cSets[0])
        cy = float(cSets[1])
        distVal = math.sqrt(math.pow((x - cx),2) + math.pow((y - cy),2))
        distArrays[distVal] = centroid
    return distArrays[min(distArrays.keys())]

def loadData():
    dFile = open('data.txt','r')
    D = []
    for line in dFile:
        D.append(line.rstrip())
    dFile.close()
    return D

def sortAndSaveClusters(dataset, clusters):
    output = open('clusters.txt','w')
    for index,data in enumerate(dataset):
        for i, clusterSet in enumerate(clusters):
            if(data in clusters[clusterSet]):
                output.write(str(index) + ' ' + str(i) + '\n')
                break

    output.close()
if __name__ == "__main__":
    D = loadData()
    K = 3
    clusters = performKMeans(K,D)
    sortAndSaveClusters(D,clusters)