import torch
from torch._C import Module
from torch.nn.modules.linear import Linear 
import torchvision
from torchvision import transforms
from PIL import Image
from os import listdir
import random
import torch.optim as optim
from torch.autograd import Variable
import torch.nn.functional as F
import torch.nn as nn

normalize = transforms.Normalize(
    mean=[0.485, 0.456, 0.406],
    std=[0.229, 0.224, 0.225]
)
transforms = transforms.Compose([ #damit die Bilder vergleichbar sind
    transforms.Resize(256),
    transforms.CenterCrop(256),
    transforms.ToTensor(),
    normalize
])

train_data_list = []
target_list = []
train_data = []
files = listdir('catdog/train/') #catdog müsste ein Ordner sein mit Bilder von Hunden und Katzen
for i in range(len(files)):
    f = random.choice(files) #pickt random bild aus ordner
    files.remove(f) #entfernt das bild sodass es nicht nochmal genommen wird
    img = Image.open("catdog/train/" + f) #lädt das bild
    img_tensor = transforms(img) #bild -> tensor 
    train_data_list.append(img_tensor)
    isCat = 1 if 'cat' in f else 0 #dateiname im train ordner enthält cat oder dog
    isDog = 1 if 'dog' in f else 0
    target = [isCat,isDog] #sagt dem netz ob bild hund oder katze ist
    target_list.append(target)
    if len(train_data_list) >= 64:
        train_data.append((torch.stack(train_data_list), target_list))
        train_data_list = [] #kein plan wieso nicht target_list = []
        print('Loaded batch ', len(train_data), 'of ', int(len(listdir('catdog/train/'))/64))
        print('Percentage Done: ', len(train_data)/int(len(listdir('catdog/train/'))/64), '%')
        #break -> break falls man erstmal nur 64 bilder zum training benutzen will

class Netz(nn.Module): #Neuronales netz
    def __init__(self): #quasi Konstrucktor
        super(Netz,self).__init__()
        self.conv1 = nn.Conv2d(3, 6, kernel_size=5) #-> kein optimiertes Netz
        self.conv2 = nn.Conv2d(6, 12, kernel_size=5)
        self.conv3 = nn.Conv2d(12, 18, kernel_size=5)
        self.conv4 = nn.Conv2d(18, 24, kernel_size=5)
        self.fc1 = nn.Linear(3456, 1000) # erst einstellbar wenn man x.size weiß, fc = fully-connected layer
        self.fc2 = nn.Linear(1000, 2) #2 weil entweder Cat oder Dog

    def forward(self, x):
        x = self.conv1(x)       #daten durch Netz jagen
        x = F.max_pool2d(x, 2)
        x = F.relu(x)
        x = self.conv2(x)
        x = F.max_pool2d(x, 2)
        x = F.relu(x)
        x = self.conv3(x)
        x = F.max_pool2d(x, 2)
        x = F.relu(x)
        x = self.conv4(x)
        x = F.max_pool2d(x, 2)
        x = F.relu(x)
        x = x.view(-1, 3456)   # -1 weil batch auslassen
        x = F.relu(self.fc1(x))
        x = self.fc2(x)
        return F.sigmoid(x) # aktivierungsfunktion für binary_cross_entropy Z.84
        #print(x.size) -> print um anzahl von Neuronen zu bekommen hier 3456, recht großes Netz
        #exit()

model = Netz()

optimizer = optim.Adam(model.parameters(), lr=0.01)
def train(epoch): #trainiert das netz
    model.train()
    batch_id = 0
    for data, target in train_data:
        target = torch.Tensor(target) #target als Tensor speichern
        data = Variable(data)
        target = Variable(target)
        optimizer.zero_grad()
        out = model(data)
        criterion = F.binary_cross_entropy
        loss = criterion(out,target)
        loss.backward()
        optimizer.step()
        print("Train Epoch: {} [{}/{} ({:.0f}%)]\tLoss: {:.6f}".format(
            batch_id * len(data), len(train_data),
            100. * batch_id / len(train_data), loss.data[0]))
        batch_id = batch_id + 1

def test(): #funktion um dog or cat zu testen
    model.eval() #model auswerten, nicht trainieren
    files = listdir('catdog/test/')
    f = random.choice(files)
    img = Image.open('catdog/test/' + f)
    img_eval_tensor = transforms(img)
    img_eval_tensor.unsqueeze_(0) #batchsize vorne drangehängt weil nur ein bild
    data = Variable(img_eval_tensor)
    out = model(data)
    print(out.data.max(1, keepdim = True)[1])
    img.show()
    x = input('') #damit es weiter geht


for epoch in range(1,30): #dauert sehr lange
    train(epoch)
test()