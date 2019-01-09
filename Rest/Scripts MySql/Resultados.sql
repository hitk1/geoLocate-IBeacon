# Consumo de Bateria
select Prontuario, Beacon_Name, Delay,  (MAX(Bateria) - MIN(Bateria)) as Consumo from registros
group by Prontuario, Beacon_Name, Delay;

#Total de registros por teste
select Beacon_Name, Delay, count(ID) as Quantidade from registros
group by Beacon_Name, Delay;

# Variação de Rssi e Distãncia
select Beacon_Name, Delay, max(Rssi) as Maior_Rssi, 
min(Distancia) as Menor_Distancia,
min(Rssi) as Menor_Rssi,
max(Distancia) as Maior_Distancia
from registros
group by Beacon_Name, Delay;

#Distancia + Desvio Padrão
select Beacon_Name, Delay, round(avg(Distancia), 4) as 'Distancia média', round(stddev(Distancia), 4) as 'Desvio Padrão'
from Registros
group by Beacon_Name, Delay;

#Tempo de Registros
select Delay, timediff(max(DataHora), min(DataHora)) as 'Tempo Total'
from Registros
group by Delay;