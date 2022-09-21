from copy import deepcopy
from PIL import Image, ImageDraw
import math
from matplotlib import pyplot as plt

class Roomba():
    def __init__(self, H=None, W=None, _str=None):
        assert ((H is not None) and (W is not None)) or (_str is not None)
        
        self.points = []
        self.origin = (0,0)
        
        if not _str:
            self.W, self.H = W, H
        else:
            vals = _str.split()       
            self.W, self.H = len(vals[0]), len(vals)
            for i in range(len(vals)):
                for j in range(len(vals[0])):
                    if vals[i][j] == 'X':
                        self.points.append((i,j))
                    if vals[i][j] == 'O':
                        self.origin = (i,j)
        
    def __repr__(self):
        _str = ""
        for i in range(len(self.H)):
            for j in range(len(self.W)):
                if (i,j) in self.points:
                    _str += 'X'
                elif (i,j) == self.origin:
                    _str += 'O'
                else:
                    _str += '-'
            _str+="\n"
        return _str
    
    def set_origin(self, origin):
        self.origin = origin
    
    def set_points(self, lst):
        self.points = lst
        
    def board_id(self):
        return hash(str(self))
    
    def show_board(self, ax=None, path=None):
        
        def set_white(im):
            alpha = im.split()[3]
            bgmask = alpha.point(lambda x: 255-x)
            im = im.convert('RGB')
            im.paste((255,255,255), None, bgmask)
            return im
        
        vert = self.W - 1
        hori = self.H - 1
        
        ax = plt.figure(figsize=(max(vert//1.5,4), max(hori//1.5,4))).gca() if ax is None else ax
        w, h = (vert+1)*50, (hori+1)*50

        hspace = w/(vert+1)
        vspace = h/(hori+1)
        
        # create a new image with a white background
        img = Image.new('RGB',(w,h),(255,255,255))
        spot = Image.open('img/spot.png')
        spot = set_white(spot)
        spot = spot.resize((math.floor(hspace), math.floor(vspace)))
        
        roomba = Image.open('img/roomba.png')
        roomba = set_white(roomba)
        roomba = roomba.resize((math.floor(hspace), math.floor(vspace)))
        
        # img = Image.new('RGB',(w,h),(255,255,255))
        draw = ImageDraw.Draw(img)

        # dirt
        for i,j in self.points:
            img.paste(spot, (math.floor(j*vspace), math.floor(i*hspace)))
            
        if path is not None:
            xprev, yprev = None, None
            for x,y in path:
                delta = 0.75*min(vspace/2, hspace/2)
                if xprev is not None:
                    draw.line((yprev*vspace+vspace/2, xprev*hspace+hspace/2,y*vspace+vspace/2, x*hspace+hspace/2), 
                              fill=(23,183,212), width=math.floor(delta//4))
                draw.ellipse((y*vspace + delta, x*hspace + delta, 
                            (y+1)*vspace-delta ,(x+1)*hspace-delta),
                            fill=(23,183,212))
                xprev, yprev = x,y
        
        # origin
        i,j = self.origin
        img.paste(roomba, (math.floor(j*vspace), math.floor(i*hspace)))
                    
        # draw axis
        draw.line((0,0,0,h),fill=(0,0,0), width=5)
        draw.line((0,0,w,0),fill=(0,0,0), width=5)
        draw.line((w,0,w,h),fill=(0,0,0), width=7)
        draw.line((0,h,w,h),fill=(0,0,0), width=7)

        for space in range(vert):
            draw.line(((space+1)*hspace,0,(space+1)*hspace,h),fill=(0,0,0), width=1)

        for space in range(hori):
            draw.line((0,(space+1)*vspace,w,(space+1)*vspace),fill=(0,0,0), width=1)

        ax.axis('off')
        ax.imshow(img)
        
        if ax is None:
            plt.show()
