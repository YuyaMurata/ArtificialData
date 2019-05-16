import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import linecache
import sys

tfile = "C:/Users/zz17807/Documents/NetBeansProjects/ArtificialData/ArtificialData/py/csv/metahist_temp.csv"
file = "csv/metahist_temp.csv"
args = sys.argv

#引数の処理
if len(args) > 1:
    file = args[1]

df = pd.read_csv(file, header=1)
title = linecache.getline(file, int(1))

s = len(df)
n = 50

#Condition
df = df.sort_values(by=['N'], ascending=False)
print(df)

if s > n:
    df = df.head(n)


#追加
adddf = pd.Series(['etc..('+str(s-n)+')', 0], index=df.columns)
df = df.append(adddf, ignore_index=True)

#df.plot()

plt.title(title)
plt.xlabel('項目ID')
plt.xticks(rotation=90)
plt.ylabel('頻度')
plt.bar(df['List'], df['N'])

plt.show()