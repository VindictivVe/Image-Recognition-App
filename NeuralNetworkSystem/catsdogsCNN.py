import torch
from torch.nn.modules.linear import Linear 
import torchvision
from torchvision import transforms
from PIL import Image
from os import listdir
import random
import torch.optim as optimizer
from torch.autograd import Variable
import torch.nn.functional as Func
import torch.nn as nn

normalizePic = transforms.Normalize(
    mean=[0.485, 0.456, 0.406],
    std=[0.229, 0.224, 0.225]
)
transformsPic = transforms.Compose([ #so pics are comparable
    transforms.Resize(256),
    transforms.CenterCrop(256),
    transforms.ToTensor(),
    normalizePic
])

tensor_data_list = [] #list of img_tensor
solution_list = [] #list of info if tensor is dog or cat
training_data = [] #stack of both lists
pictureFiles = listdir('cats_and_dogs/train_dataset/')
for i in range(len(pictureFiles)):
    file = random.choice(pictureFiles) #random pic from train dataset
    pictureFiles.remove(file) #removes pic so no repetition
    pic = Image.open("cats_and_dogs/train_dataset/" + file) #loads pic
    pic_tensor = transforms(pic) #transform pic to tensor 
    tensor_data_list.append(pic_tensor)
    fileIsCat = 1 if 'cat' in file else 0 #checks if file is cat or dog
    fileIsDog = 1 if 'dog' in file else 0
    fileSolution = [fileIsCat,fileIsDog] #only one is true
    solution_list.append(fileSolution) 
    if len(tensor_data_list) >= 64: #all 64 saving training data
        training_data.append((torch.stack(training_data), solution_list))
        training_data = []

class CNN(nn.Module): #neuronale net
    def __init__(self): #'construcktor'
        super(CNN,self).__init__()
        self.conv1 = nn.Conv2d(3, 6, kernel_size=5) #no optimized net (numbers)
        self.conv2 = nn.Conv2d(6, 12, kernel_size=5)
        self.conv3 = nn.Conv2d(12, 18, kernel_size=5)
        self.conv4 = nn.Conv2d(18, 24, kernel_size=5)
        self.fc1 = nn.Linear(3456, 1000) #only known if x.size is known, fc = fully-connected layer
        self.fc2 = nn.Linear(1000, 2) #2 cause either cat or dog

    def forward(self, x):
        x = self.conv1(x)       #data input to net
        x = Func.max_pool2d(x, 2)
        x = Func.relu(x)
        x = self.conv2(x)
        x = Func.max_pool2d(x, 2)
        x = Func.relu(x)
        x = self.conv3(x)
        x = Func.max_pool2d(x, 2)
        x = Func.relu(x)
        x = self.conv4(x)
        x = Func.max_pool2d(x, 2)
        x = Func.relu(x)
        x = x.view(-1, 3456)   # -1 to leave out batch 
        x = Func.relu(self.fc1(x))
        x = self.fc2(x)
        return Func.sigmoid(x) #activation function for binary_cross_entropy Z.84
        #print(x.size) -> to know x.size() = 3456
        #exit()

catOrDog = CNN()
netOptimizer = optimizer.Adam(catOrDog.parameters(), lr=0.01)

def train(epoch): #trains the net
    catOrDog.train()
    batch_id = 0
    for tensor, solution in training_data:
        solution = torch.Tensor(solution) #solution to tensor
        tensor = Variable(tensor) #tensors as variable
        solution = Variable(solution)
        optimizer.zero_grad()
        out = catOrDog(tensor)
        criterion = Func.binary_cross_entropy
        loss = criterion(out,solution)
        loss.backward()
        optimizer.step()
        print("Train Epoch: {} [{}/{} ({:.0f}%)]\tLoss: {:.6f}".format(
            batch_id * len(tensor), len(training_data),
            100. * batch_id / len(training_data), loss.data[0]))
        batch_id = batch_id + 1

def isCatOrDog(): #test funktion for net
    catOrDog.eval() #CNN eval mode
    pictureFiles = listdir('catdog/test/') #get picture from android
    file = random.choice(pictureFiles)
    pic = Image.open('catdog/test/' + file)
    pic_eval_tensor = transforms(pic)
    pic_eval_tensor.unsqueeze_(0) #batchsize in front cause only 1 pic
    data = Variable(pic_eval_tensor)
    out = catOrDog(data)
    print(out.data.max(1, keepdim = True)[1])
    pic.show()
    x = input('') #testing for several pictures
