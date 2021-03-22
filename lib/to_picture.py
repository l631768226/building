# -*- coding: utf-8 -*-
import shapefile
import cv2
import numpy as np
from shapely.geometry import Polygon

def minus(a):
    a = np.array([-x for x in a])
    return a

def picture_show(landuse_path):

    a = 1024
    b = 512
    landuse = shapefile.Reader(landuse_path, encoding='gbk')

    k = 0
    img = np.zeros((a, a, 3))
    img.fill(255)

    l_shape = landuse.shape(k)  #######要改
    l_convex = Polygon(l_shape.points).convex_hull
    x_c = l_convex.centroid.xy[0][0] #l_convex.centroid.xy:(array('d', [12945692.760656377]), array('d', [4861576.219346005]))
    y_c = l_convex.centroid.xy[1][0]

    l_dot = np.array(l_shape.points)

    l_nom_x = np.array(list(map(int, l_dot[:, 0]))) - int(x_c)
    l_nom_y = np.array(list(map(int, l_dot[:, 1]))) - int(y_c)
    l_inter = np.concatenate((l_nom_x[:, np.newaxis] + b, minus(l_nom_y)[:, np.newaxis] + b),1)  # nom_x[:, np.newaxis]新增一个维度
    cv2.polylines(img, [np.asarray(l_inter)], True, (0,0,255), 1) # cv2.polylines(画布，点坐标列表，封闭，颜色，宽度)polylines点坐标不能出现浮点型，需要是整型
    cv2.imwrite('./picture.jpg', img)
