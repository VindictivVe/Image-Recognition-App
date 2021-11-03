import torch 
import torchvision
from torchvision import transforms
from PIL import Image
from os import listdir
import random
import torch.optim as optim
from torch.autograd import Variable
import torch.nn.functional as F

normalize = transforms.Normalize(
    mean=[0.485, 0.456, 0.406],
    std=[0.229, 0.224, 0.225]
)
transform = transforms.Compose([
    transforms.Resize(256),
    transforms.CenterCrop(256),
    transforms.ToTensor(),
    normalize
])

train_data_list = []
target_list = []
train_data = []
files = listdir('catdog/train/')
for i in range(len(files)):
    f = random.choice(files)
    files.remove(f)
    img = Image.open("catdog/train/" + f)
    img_tensor = transform(img)
    train_data_list.append(img_tensor)
    isCat = 1 if 'cat' in f else 0
    isDog = 1 if 'dog' in f else 0
    target = [isCat,isDog]
    target_list.append(target)
    if len(train_data_list) >= 64:
        train_data.append((torch.stack(train_data_list), target_list))
        train_data_list = []
        break

optimizer = optim.Adam(model.parameters(), lr=0.01)
def train(epoch):
    model.train()
    batch_id = 0
    for data, target in train_data:
        target = torch.Tensor(target)
        data = Variable(data)
        target = Variable(target)
        optimizer.zero_grad()
        out = model(data)
        criterion = F.nll_loss
        loss = criterion(out,target)
        loss.backward()
        optimizer.step()
        print("Train Epoch: {} [{}/{} ({:.0f}%)]\tLoss: {:.6f}".format(
            batch_id * len(data), len(train_data),
            100. * batch_id / len(train_data), loss.data[0]))
        batch_id = batch_id + 1

for epoch in range(1,30):
    train(epoch)