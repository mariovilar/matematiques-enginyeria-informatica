# maze.py
import random
import networkx as nx
from matplotlib import pyplot as plt
from IPython.display import SVG, display

# Base code Create a maze using the depth-first algorithm described at
# https://scipython.com/blog/making-a-maze/
# Christian Hill, April 2017.

class Cell:
    """A cell in the maze.

    A maze "Cell" is a point in the grid which may be surrounded by walls to
    the north, east, south or west.

    """

    # A wall separates a pair of cells in the N-S or W-E directions.
    wall_pairs = {'N': 'S', 'S': 'N', 'E': 'W', 'W': 'E'}

    def __init__(self, i, x, y):
        """Initialize the cell at (x,y). At first it is surrounded by walls."""

        self.id = i
        self.x, self.y = x, y
        self.walls = {'N': True, 'S': True, 'E': True, 'W': True}
        self.value = 0
        self.color = (0,255,255)

    def has_all_walls(self):
        """Does this cell still have all its walls?"""

        return all(self.walls.values())

    def knock_down_wall(self, other, wall, graph):
        """Knock down the wall between cells self and other."""

        self.walls[wall] = False
        other.walls[Cell.wall_pairs[wall]] = False
        
        if self.id > other.id:
            self, other = other, self
        graph.add_edge(self.id, other.id)
        
        
    def build_wall(self, other, wall, graph):
        self.walls[wall] = True
        other.walls[Cell.wall_pairs[wall]] = True
        
        if self.id > other.id:
            self, other = other, self
        graph.remove_edge(self.id, other.id)


class Maze:
    """A Maze, represented as a grid of cells."""

    def __init__(self, x_num, y_num, seed=None, p_down=0.2, p_build=0, directed=False):
        """Initialize the maze grid.
        The maze consists of x_num x y_num cells
        """

        self.x_num, self.y_num = x_num, y_num
        self.maze_map = [[Cell(x_num*y + x, x, y) for y in range(y_num)] for x in range(x_num)]
        self.path = []
        self.directed = directed
        self.G = nx.Graph() if not directed else nx.DiGraph() 
        
        random.seed(seed)
        self.make_maze(p_down, p_build)
#         self._make_graph()
           
        
    def cell_at(self, x, y):
        """Return the Cell object at (x,y)."""

        return self.maze_map[x][y]

    def __str__(self):
        """Return a (crude) string representation of the maze."""

        maze_rows = ['-' * self.x_num * 2]
        for y in range(self.y_num):
            maze_row = ['|']
            for x in range(self.x_num):
                if self.maze_map[x][y].walls['E']:
                    maze_row.append(' |')
                else:
                    maze_row.append('  ')
            maze_rows.append(''.join(maze_row))
            maze_row = ['|']
            for x in range(self.x_num):
                if self.maze_map[x][y].walls['S']:
                    maze_row.append('-+')
                else:
                    maze_row.append(' +')
            maze_rows.append(''.join(maze_row))
        return '\n'.join(maze_rows)

    
    def get_svg(self, height=200, plot_path=False):
        aspect_ratio = self.x_num / self.y_num
        # Pad the maze all around by this amount.
        padding = 10
        # Height and width of the maze image (excluding padding), in pixels
        width = int(height * aspect_ratio)
        # Scaling factors mapping maze coordinates to image coordinates
        scy, scx = height / self.y_num, width / self.x_num
        
        def write_wall(ww_x1, ww_y1, ww_x2, ww_y2):
            """Write a single wall to the SVG image file handle f."""

            return '<line x1="{}" y1="{}" x2="{}" y2="{}"/>'.format(ww_x1, ww_y1, ww_x2, ww_y2)
            
        svg_txt = ""
        # SVG preamble and styles.
        svg_txt += '<?xml version="1.0" encoding="utf-8"?>'
        svg_txt += '<svg xmlns="http://www.w3.org/2000/svg"'
        svg_txt += '    xmlns:xlink="http://www.w3.org/1999/xlink"'
        svg_txt += '    width="{:d}" height="{:d}" viewBox="{} {} {} {}">'.format(
            width + 2 * padding, height + 2 * padding,
            -padding, -padding, width + 2 * padding, height + 2 * padding)
            
        svg_txt += '<defs>\n<style type="text/css"><![CDATA['
        svg_txt += 'line {'
        svg_txt += '    stroke: #000000;\n    stroke-linecap: square;'
        svg_txt += '    stroke-width: 5;\n}'
        svg_txt += ']]></style>\n</defs>'
        
        # Draw the "South" and "East" walls of each cell, if present (these
        # are the "North" and "West" walls of a neighbouring cell in
        # general, of course).
        for x in range(self.x_num):
            for y in range(self.y_num):
                if self.cell_at(x, y).walls['S']:
                    x1, y1, x2, y2 = x * scx, (y + 1) * scy, (x + 1) * scx, (y + 1) * scy
                    svg_txt += write_wall(x1, y1, x2, y2)
                if self.cell_at(x, y).walls['E']:
                    x1, y1, x2, y2 = (x + 1) * scx, y * scy, (x + 1) * scx, (y + 1) * scy
                    svg_txt += write_wall(x1, y1, x2, y2)
                                        
        # Draw path
        if len(self.path)>1 and plot_path:
            for i in range(len(self.path)-1):   
                px, py = self.path[i]
                npx, npy = self.path[i+1]            
                mx, my = min(px, npx), min(py, npy)            
                svg_txt += '<rect x="{}" y="{}" width="{}" height="{}" style="fill:rgb(255,0,0)" />'.format(mx*scx+scx/3, my*scy+scy/3, scx*abs(npx-px)+scx/4, scy*abs(npy-py)+scy/4)         

            x0, y0 = self.path[0]
            xn, yn = self.path[-1]
            svg_txt += '<rect x="{}" y="{}" width="{}" height="{}" style="fill:rgb(0,255,0)" />'.format(x0*scx+scx/4, y0*scy+scy/4, scx/2, scy/2)
            svg_txt += '<rect x="{}" y="{}" width="{}" height="{}" style="fill:rgb(0,0,255)" />'.format(xn*scx+scx/4, yn*scy+scy/4, scx/2, scy/2)
                                    
        # Draw objects
        for x in range(self.x_num):
            for y in range(self.y_num):
                c = self.cell_at(x,y)
                if c.value != 0:
                    svg_txt += '<rect x="{}" y="{}" width="{}" height="{}" style="fill:rgb({},{},{})" />'.format(x*scx+scx/4, y*scy+scy/4, scx/2, scy/2, c.color[0], c.color[1], c.color[2])
    
            
        # Draw the North and West maze border, which won't have been drawn
        # by the procedure above.
        svg_txt += '<line x1="0" y1="0" x2="{}" y2="0"/>'.format(width)
        svg_txt += '<line x1="0" y1="0" x2="0" y2="{}"/>'.format(height)
        svg_txt += '</svg>'
        return svg_txt
        
    def write_svg(self, filename, height=200, plot_path=False):
        """Write an SVG image of the maze to filename."""

        with open(filename, 'w') as f:
            print(self.get_svg(height=height, plot_path=plot_path), file=f)
                        
    def display_maze(self, height=200, plot_path=False):                
        display(SVG(self.get_svg(height=height, plot_path=plot_path)))                  
        
    
    def maze_graph(self):
        return self.G
        

    def find_valid_neighbours(self, cell):
        """Return a list of unvisited neighbours to cell."""

        if self.directed:
            delta = [('E', (1, 0)),
                 ('S', (0, 1))]
        else:
            delta = [('W', (-1, 0)),
                 ('E', (1, 0)),
                 ('S', (0, 1)),
                 ('N', (0, -1))]
            
        
        neighbours = []
        for direction, (dx, dy) in delta:
            x2, y2 = cell.x + dx, cell.y + dy
            if (0 <= x2 < self.x_num) and (0 <= y2 < self.y_num):
                neighbour = self.cell_at(x2, y2)
                if neighbour.has_all_walls():
                    neighbours.append((direction, neighbour))
        return neighbours
    
    def find_all_neighbours(self, cell):
        """Return a list of all neighbours to cell."""

        if self.directed:
            delta = [('E', (1, 0)),
                 ('S', (0, 1))]
        else:
            delta = [('W', (-1, 0)),
                 ('E', (1, 0)),
                 ('S', (0, 1)),
                 ('N', (0, -1))]
            
        neighbours = []
        for direction, (dx, dy) in delta:
            x2, y2 = cell.x + dx, cell.y + dy   
            if (0 <= x2) and (x2 < self.x_num) and (0 <= y2) and (y2 < self.y_num):
                neighbour = self.cell_at(x2, y2)
                neighbours.append((direction, neighbour))
        return neighbours

    
    def wall_can_be_removed(self, cell, next_cell, direct):
        if direct=='E':   
            if not cell.walls['E']:
                return False
                
            if cell.y==0:
                inf_cell = self.cell_at(cell.x, 1)
                if (not cell.walls['S']) and (not inf_cell.walls['E']) and (not next_cell.walls['S']):
                    return False
                return True           
                    
            if cell.y==self.y_num-1:
                sup_cell = self.cell_at(cell.x, self.y_num-2)
                if (not cell.walls['N']) and (not sup_cell.walls['E']) and (not next_cell.walls['N']):
                    return False
                return True           
             
            else:
                inf_cell = self.cell_at(cell.x, cell.y+1)
                sup_cell = self.cell_at(cell.x, cell.y-1)
                                
                if ((not cell.walls['N']) and (not sup_cell.walls['E']) and (not next_cell.walls['N'])) or ((not cell.walls['S']) and (not inf_cell.walls['E']) and (not next_cell.walls['S'])):
                    return False
                return True
            
        # South
        else:
            if not cell.walls['S']:
                return False
            
            if cell.x==0:
                east_cell = self.cell_at(1, cell.y)
                if (not cell.walls['E']) and (not east_cell.walls['S']) and (not next_cell.walls['E']):
                    return False
                return True           
                    
            if cell.x==self.x_num-1:
                west_cell = self.cell_at(self.x_num-2, cell.y)
                if (not cell.walls['W']) and (not west_cell.walls['S']) and (not next_cell.walls['W']):
                    return False
                return True           
             
            else:
                east_cell = self.cell_at(cell.x+1, cell.y)
                west_cell = self.cell_at(cell.x-1, cell.y)
                
                if ((not cell.walls['E']) and (not east_cell.walls['S']) and (not next_cell.walls['E'])) or ((not cell.walls['W']) and (not west_cell.walls['S']) and (not next_cell.walls['W'])):
                    return False
                return True
                    
    
    def knock_down_more_walls(self, p=0.05):
        for y in range(self.y_num):
            for x in range(self.x_num):
                cell = self.cell_at(x,y)
                neighbours = self.find_all_neighbours(cell)
                neighbours = [n for n in neighbours if n[0] in ['S', 'E']]
                for direct, nei in neighbours:
                    if self.wall_can_be_removed(cell, nei, direct):
                        if random.random()<p:                                                        
                            cell.knock_down_wall(nei, direct, self.G)                               
                                                                    
    def build_more_walls(self, p=0.05):
        for y in range(self.y_num):
            for x in range(self.x_num):
                cell = self.cell_at(x,y)
                neighbours = self.find_all_neighbours(cell)
                neighbours = [n for n in neighbours if n[0] in ['S', 'E']]                            
                if len(neighbours)==0:
                    continue
                direct, nei = random.choice(neighbours)
                if not cell.walls[direct]:
                    if random.random()<p:
                        cell.build_wall(nei, direct, self.G)                         
        
        
    def make_maze(self, p_down=0.2, p_build=0):
        # Total number of cells.
        n = self.x_num * self.y_num
        self.G.add_nodes_from(list(range(n)))
        
        cell_stack = []
        ix, iy = 0,0
        current_cell = self.cell_at(ix, iy)
        
        # Total number of visited cells during maze construction.
        nv = 1

        while nv < n:
            neighbours = self.find_valid_neighbours(current_cell)

            if not neighbours:
                
                if self.directed:
                    random.shuffle(cell_stack)
                # We've reached a dead end: backtrack.                
                current_cell = cell_stack.pop()
                continue

            # Choose a random neighbouring cell and move to it.
            direction, next_cell = random.choice(neighbours)
            current_cell.knock_down_wall(next_cell, direction, self.G)                        
            
            cell_stack.append(current_cell)
            
            current_cell = next_cell
            nv += 1 
            
        self.knock_down_more_walls(p=p_down)
        self.build_more_walls(p=p_build)
        
    def set_path(self, path):
        if path==[]:
            self.path==path
            return
        
        # List of coordinates
        if isinstance(path[0], tuple) or isinstance(path[0], list):
            self.path = path
            
        # List of nodes
        else:
            self.path = []
            for node in path:
                c = self.maze_map[node%self.x_num][node//self.x_num]
                self.path.append((c.x, c.y))
                
                
    def set_value(self, node, v, color=(0,255,255)):
        x,y = node%self.x_num, node//self.x_num
        self.cell_at(x,y).value = v
        self.cell_at(x,y).color = color
        
    def set_values(self, lst, v, color=(0,255,255)):
        for n in lst:
            self.set_value(n,v,color)
