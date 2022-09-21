# +
from copy import deepcopy
from matplotlib import pyplot as plt
from PIL import Image, ImageDraw

class LightsOut():
    def __init__(self, H=None, W=None, _str=None):
        assert ((H is not None) and (W is not None)) or (_str is not None)
        if not _str:
            self.grid = [['-' for _ in range(W)] for _ in range(H)]
        else:
            vals = _str.split()
            self.grid = [[vals[i][j] for j in range(len(vals[0]))] for i in range(len(vals))]
    
    def click(self, i, j):
        _grid = deepcopy(self.grid)
        assert (i>=0) and (j>=0) and (i<len(self.grid)) and (j<len(self.grid[0]))
        neighbours = [(i,j), (i-1,j), (i+1, j), (i, j-1), (i, j+1)]
        for x, y in neighbours:
            if (x>=0) and (y>=0) and (x<len(self.grid)) and (y<len(self.grid[0])):
                _grid[x][y] = "-" if _grid[x][y]=="X" else 'X'
        new_lo = LightsOut(1,1)
        new_lo.grid = _grid
        return new_lo
                
    def board_id(self):
        return hash(str(self))
           

    def __repr__(self):
        _str = ""
        for i in range(len(self.grid)):
            for j in range(len(self.grid[0])):
                _str += str(self.grid[i][j])
            _str+="\n"
        return _str
    
    def __lt__(self, other):
        return False
    
    
    def show_board(self, ax=None, show_click=None):
        
        vert = len(self.grid[0])-1
        hori = len(self.grid)-1
        
        ax = plt.figure().gca() if ax is None else ax
        w, h = (vert+1)*50, (hori+1)*50

        # create a new image with a white background

        img = Image.new('RGB',(w,h),(255,255,255))
        draw = ImageDraw.Draw(img)

        hspace = w/(vert+1)
        vspace = h/(hori+1)

        # lights
        for i in range(len(self.grid)):
            for j in range(len(self.grid[0])):
                if self.grid[i][j]=='X':
                    draw.rectangle((j*vspace, i*hspace, (j+1)*vspace,(i+1)*hspace), fill=(0,0,255))
                    
        # click
        if show_click is not None:
            xclick, yclick = show_click
            delta = 0.25*min(vspace/2, hspace/2)
            draw.ellipse((yclick*vspace+delta, xclick*hspace+delta, (yclick+1)*vspace-delta,(xclick+1)*hspace-delta),fill=(255,0,0))
    
            neighbours = [(xclick-1,yclick), (xclick+1, yclick), (xclick, yclick-1), (xclick, yclick+1)]
            for x, y in neighbours:
                if (x>=0) and (y>=0) and (x<len(self.grid)) and (y<len(self.grid[0])):
                    delta = 0.75*min(vspace/2, hspace/2)
                    draw.ellipse((y*vspace + delta, x*hspace + delta, 
                               (y+1)*vspace-delta ,(x+1)*hspace-delta),
                              fill=(255,0,0))

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
