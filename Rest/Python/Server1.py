from flask import Flask, request, jsonify
import datetime
import json
import pymysql as conn
import hashlib
from random import randint
from collections import Counter

app = Flask(__name__)
INVALID = "INVALID"
INVALID_TOKEN = "INVALID_TOKEN"
ERRO = "ERRO"
EXPIRES = "TOKEN_EXPIRADO"
ACCEPT = "ACCEPT"


def TrataString(text):
    text = text.replace("'", "")
    text = text.upper()

    return text

@app.route('/register', methods=['POST'])
def RegistrarUsuario():
    try:
        db = DBase()
        json_data = request.get_json()
        prontuario = TrataString(json_data['prontuario'])
        password = TrataString(json_data['passwd'])

        if (prontuario is not None and password is not None):
            token = db.GetUser_Grant(prontuario)
            if(token is None):
                token = db.SaveUser(prontuario, password)
        else:
            return ERRO

        return jsonify(token)
    except Exception as ex:
        return ERRO

@app.route('/refresh', methods=['POST'])
def Refresh():  ##Atualização do Token
    try:
        db = DBase()

        json = request.get_json()
        grant = TrataString(json['grant'])

        if(grant is not None):
            state = db.VerificaGrant(grant)

            if(state is True):      #Verifico se o token é válido
                usuario = db.GetUser(grant)
                token = db.Gera_Autorizacao(usuario[1], usuario[3])

                return jsonify(token)
            else:
                return INVALID_TOKEN
        else:
           return ERRO
    except Exception as Ex:
        return ERRO

@app.route('/transfer', methods=['POST'])
def Registra():
    try:
        db = DBase()

        jsonList = request.get_json()
        token = TrataString(jsonList['token'])

        autorizacao = db.RetornaToken(token)
        if(autorizacao is not None):
            dataNow = datetime.datetime.now()
            tempo = autorizacao[3] - dataNow

            if(tempo.days >= 0 and tempo.seconds > 60):
                registros = jsonList['registros']
                rows = db.Inserir(registros)
                print(str(rows))

                return ACCEPT
            else:
                return EXPIRES
        else:
            return INVALID_TOKEN
        return ''
    except Exception as Ex:
        print(Ex)
        return ERRO




class DBase:

    def Open(self):
        return conn.connect(host="localhost", user="root", passwd="root", db="IBeacon")

    def Encrypt(self, type, string):
        hash = hashlib.sha256()

        # se type == 0, gero o hash do toke grant a partir do prontuario
        # se nao, gero o hash da senha
        if (type == 0):
            hash.update(str.encode(string + str(randint(0, 10000))))
        else:
            hash.update(str.encode(string))
        return hash.hexdigest().upper()

    def SaveUser(self, user, passwd):
        token = self.Encrypt(0, user)

        query = "INSERT INTO Usuarios (Prontuario, Passwd, Tk_Grant) VALUES(%s, %s, %s)"
        values = user, passwd, token

        try:
            con = self.Open()
            cursor = con.cursor()

            # Insert
            cursor.execute(query, values)

            cursor.close()
            con.commit()
            con.close()

            return token
        except Exception as eX:
            print(eX)

    def GetUser(self, token):
        query = "SELECT * FROM Usuarios WHERE Tk_Grant = %s"

        try:
            con = self.Open()
            cursor = con.cursor()

            cursor.execute(query, token)
            usuario = cursor.fetchone()

            con.close()
            cursor.close()
            return usuario
        except Exception as Ex:
            print(Ex)

    def GetUser_Grant(self, user):

        query = "SELECT Tk_Grant FROM Usuarios WHERE Prontuario = %s"
        values = user

        try:
            con = self.Open()
            cursor = con.cursor()

            cursor.execute(query, values)
            token = cursor.fetchone()

            con.close()
            cursor.close()
            return token
        except Exception as Ex:
            print(Ex)

    def Gera_Autorizacao(self, prontuario, token):
        # O token é gerado pelo token de consessão concatenado com um random de 0 a 10000
        token = self.Encrypt(1, token + "|" + str(randint(0, 10000)))

        # O tempo to token é do exato momento da execução a um dia
        time = datetime.datetime.now() + datetime.timedelta(days=1)

        query = "INSERT INTO Tokens (Prontuario, Tk_Autorizacao, Tk_Tempo) VALUES(%s, %s, %s)"
        values = prontuario, token, time

        try:
            con = self.Open()
            cursor = con.cursor()

            cursor.execute(query, values)
            con.commit()
            cursor.close()
            con.close()

            return token
        except Exception as Ex:
            print(Ex)

    def VerificaGrant(self, grant):

        query = "SELECT CASE WHEN Prontuario IS NULL THEN 'False' WHEN Prontuario = '' THEN 'False' ELSE 'True' END AS Prontuario FROM Usuarios WHERE Tk_Grant = %s"

        try:
            con = self.Open()
            cursor = con.cursor()

            cursor.execute(query, grant)

            state = cursor.fetchone()

            if(state is not None):
                return True
            return False
        except Exception as Ex:
            print(Ex)

    def RetornaToken(self, token):

        query = "SELECT * FROM Tokens WHERE Tk_Autorizacao = %s ORDER BY ID DESC LIMIT 1"

        try:
            con = self.Open()
            cursor = con.cursor()

            cursor.execute(query, token)
            token = cursor.fetchone()

            return token
        except Exception as ex:
            print(ex)


    def Inserir(self, list):
        query = "INSERT INTO Registros (Prontuario, ID_Beacon, Beacon_Name, Rssi, Dbm, Distancia, Bateria, Delay, DataHora)" \
                "VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)"

        try:
            con = self.Open()
            cursor = con.cursor()
            count = 0

            for registro in list:
                values = (registro['prontuario'],
                          registro['idBeacon'],
                          registro['beaconName'],
                          registro['rssi'],
                          registro['dbm'],
                          round(float(registro['distancia']), 2),
                          registro['bateria'],
                          registro['delay'],
                          datetime.datetime.strptime(registro['dataHora'], '%d/%m/%Y %H:%M:%S'))
                count = count + cursor.execute(query, values)

            cursor.close()
            con.commit()
            con.close()

            return count

        except Exception as Ex:
            print(Ex)

