import pymysql as conn

def Max_Distance(data):
    try:
        c_maxDistance = con.cursor()
        c_maxDistance.execute("SELECT Id, Beacon, Rssi, Distancia, DataHora FROM Registros WHERE Distancia = (SELECT MAX(Distancia) FROM Registros WHERE (STR_TO_DATE(DataHora, '%d/%m/%Y') = '" + str(data) + "') LIMIT 1 ) LIMIT 1")

        for (Id, Beacon, Rssi, distance, datahora) in c_maxDistance:
            arquivo.write("\nDistancia maxima do dia:\n")
            arquivo.write("ID: " + str(Id) + "\tBeacon:" + Beacon + "\tRssi:" + str(Rssi) + "\tDistancia: " + str(float(distance)) + "\tHorario:" + datahora + "\n")

        c_maxDistance.close()
    except Exception:
        pass

def Frequency(d):
    try:
        c_qtd = con.cursor()
        c_qtd.execute("SELECT Beacon_Name, COUNT(ID) FROM Registros WHERE STR_TO_DATE(DataHora, '%d/%m/%Y') = '" + str(d) + "' GROUP BY Beacon_Name")
        arquivo.write("\n\nFrequencia de registros:\n")
        for (c_beacon, qtd) in c_qtd:
            arquivo.write("Beacon: " + str(c_beacon) + " - " + str(qtd) + " vezes\n")
        c_qtd.close()

        #Intervalo do tempo de teste
        c_tempo = con.cursor()
        c_tempo.execute("SELECT TIMEDIFF((SELECT STR_TO_DATE(MAX(DataHora), '%d/%m/%Y %H:%i:%s') from Registros where str_to_date(DataHora, '%d/%m/%Y') = '" + str(d) + "'), (select str_to_date(min(DataHora), '%d/%m/%Y %H:%i:%s') from Registros where str_to_date(DataHora, '%d/%m/%Y') = '" + str(d)+ "'))")
        for tempo in c_tempo:
            arquivo.write("\nNo intervalo de tempo de: " + str(tempo[0]))
        c_tempo.close()

    except Exception:
        pass

def Details_Rssi(data):
    try:
        strWhere = "(STR_TO_DATE(DataHora, '%d/%m/%Y') = '" + str(data) + "')"

        c_RssiMax = con.cursor()
        c_RssiMax.execute("SELECT Id, Beacon, Rssi, Distancia, DataHora FROM Registros WHERE Rssi = (SELECT MAX(Rssi) FROM Registros WHERE %s" % strWhere + ") LIMIT 1")

        for (Id, Beacon, Rssi, distance, datahora) in c_RssiMax:
            arquivo.write("\nMaior forca de sinal: " + str(Rssi) + "\t Horario: " + datahora)
            arquivo.write("\nID: " + str(Id) + "\tBeacon:" + Beacon + "\tRssi:" + str(Rssi) + "\tDistancia: " + str(float(distance)) + "\tHorario:" + datahora + "\n")

        c_RssiMax.close()

        c_RssiMin = con.cursor()
        c_RssiMin.execute("SELECT Id, Beacon, Rssi, Distancia, DataHora FROM Registros WHERE Rssi = (SELECT MIN(Rssi) FROM Registros WHERE %s" % strWhere + ") LIMIT 1")

        for(Id, Beacon, Rssi, distance, datahora) in c_RssiMin:
            arquivo.write("\nMenor forca de sinal: " + str(Rssi) + "\t Horario: " + datahora)
            arquivo.write("\nID: " + str(Id) + "\tBeacon:" + Beacon + "\tRssi:" + str(Rssi) + "\tDistancia: " + str(float(distance)) + "\tHorario:" + datahora + "\n")

        arquivo.write("\n----------------------------------------------------------------------------------")
        c_RssiMin.close()
    except Exception:
        pass

def FrequencyByUser(d):
    try:
        c_user = con.cursor()
        c_user.execute("SELECT Prontuario, Beacon_Name, COUNT(Beacon_Name) FROM Registros where STR_TO_DATE(DataHora, '%d/%m/%Y') = '" + str(d) + "' GROUP BY Prontuario, Beacon_Name")

        arquivo.write("\n\n----------------------------------------------------------------------------------\n")
        arquivo.write("Frequencia de Registros por Usuarios:\n")

        for pront, beacon, qtd in c_user:
            arquivo.write(pront + ": " + beacon + " - " + str(qtd) +" vezes\n")

        arquivo.write("----------------------------------------------------------------------------------\n")
        c_user.close()
        
    except Exception:
        pass

def BatteryUsage(d):
    try:

        arquivo.write("\nConsumo de Bateria por Usuarios:\n")

        c_pront = con.cursor()
        c_pront.execute("SELECT DISTINCT Prontuario FROM Registros WHERE STR_TO_DATE(DataHora, '%d/%m/%Y') = '" + str(d) + "'")

        for prontuario in c_pront:
            pront = (str(prontuario).replace("'","").replace("(", "").replace(")", "").replace(",", ""))
            c_calculer = con.cursor()
            c_calculer.execute("SELECT MAX(Bateria) FROM Registros WHERE Prontuario = '" + pront + "' and STR_TO_DATE(DataHora, '%d/%m/%Y') = '" + str(d) + "'")

            maximo = c_calculer.fetchone()
            maximo = int(maximo[0])

            c_calculer.execute("SELECT MIN(Bateria) FROM Registros WHERE Prontuario = '" + pront + "' and STR_TO_DATE(DataHora, '%d/%m/%Y') = '" + str(d) + "'")
            minimo = c_calculer.fetchone()
            minimo = int(minimo[0])
            

            arquivo.write("Prontuario: " + pront + " - " + str(maximo - minimo)  + "% consumido")
            c_calculer.close()

        c_pront.close()
        arquivo.write("\n----------------------------------------------------------------------------------\n")
    except Exception as ex:
        arquivo.write(str(ex))

def Registro_Dia():
    try:
        c_datas = con.cursor()
        c_datas.execute("SELECT STR_TO_DATE(DataHora, '%d/%m/%Y') as date FROM Registros GROUP BY date")

        for(datas) in c_datas:
            for(d) in datas:
                ##Registro por dias
                arquivo.write("\nRegistros do dia [" + str(d) + "]: \n")
                arquivo.write("----------------------------------------------------------------------------------\n")
                arquivo.write("Id\t|Prontuario\t|ID_Beacon\t|Beacon\t|Rssi\t|dBm\t|Distancia(Metros)\t|Nivel de Bateria (%)\t|Intervalo de Scan\t|Data/Hora\n")

                c_details = con.cursor()
                c_details.execute("SELECT ID, Prontuario, ID_Beacon, Beacon_Name, Rssi, Dbm, Distancia, Bateria, Delay, DataHora FROM Registros WHERE (STR_TO_DATE(DataHora, '%d/%m/%Y') = '" + str(d) + "')")

                for(Id, Prontuario, ID_Beacon, beacon, Rssi, dBm, Distancia, Bateria, Delay, DataHora) in c_details:
                    s_beacon = ""
                    if((Beacon == "Mint") or (Beacon == "Ice")):
                        s_beacon = "\t" + Beacon
                    else:
                        s_beacon = "Blueberry"
                    arquivo.write(str(Id) +"\t|" + Prontuario + "\t|" + str(ID_Beacon) + "\t|" + Beacon_Name + "\t|" + str(Rssi) + "\t|" + str(dBm) + "\t|" + str(float(Distancia)) + "\t\t        |"+ str(Bateria) + "\t\t\t|" + str(Delay) + "\t\t\t|" + DataHora + "\n")
                c_details.close()
                arquivo.write("\n------------------------------------------------------------------------\n\n")
                
                ##Maior distancia
                #Max_Distance(d)

                ##Rssi
                #Details_Rssi(d)

                #Frequencia de registro
                Frequency(d)

                #Frequencia por Prontuario
                FrequencyByUser(d)                

                BatteryUsage(d)
                
        c_datas.close()
    except Exception:
        pass

    

try:
    con = conn.connect(host="localhost", user="root", passwd="root", db="IBeacon")
    cursor = con.cursor()

    cursor.execute("SELECT * FROM Registros;")
    
    with open('C:\\Users\\HIT\\Desktop\\Relatorio.txt', 'w') as arquivo:

        Registro_Dia()

        arquivo.write("\n\nObservacoes:")
        arquivo.write("\nA distancia e calculada pela biblioteca [EstimoteSDK.RegionUtils] da Estimote, disponivel no github.\n")
        arquivo.write("""O Rssi e considerado a "Forca do Sinal" no momento do scanning. """)
        arquivo.write("\nO dBm e o ganho de transmissao, este, quanto maior mais intenso e o sinal.")

        c_count = con.cursor()
        qtd = c_count.execute("SELECT Id FROM Registros;")
        arquivo.write("\n\nTotal de Registros: %s" % str(qtd))
        arquivo.close()
        
    con.close()

except KeyboardInterrupt:
    pass


    
