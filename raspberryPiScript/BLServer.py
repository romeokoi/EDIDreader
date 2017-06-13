from time import sleep

from bluetooth import *
import binascii
import json
import struct
import subprocess

class EdidParser():

    def __init__(self,edid):
        self.edid = edid
        self.manufacturers = {'AAA':'Avolites','ACI':'Ancor ','ACR':'Acer','APP':'Apple','BNO':'Bang & Olufsen',
                              'CMN':'Chimei','CMO':'Chi Mei', 'CRO': 'Extraordinary Technologies PTY', 'DEL':'Dell',
                              'DON':'DENON', 'ENC':'Eizo Nanao', 'EPH':'Epiphan','FUS':'Fujitsu', 'GSM':'Goldstar',
                              'HIQ':'Kaohsiung', 'HSD':'HannStar','HWP':'HP', 'INT': 'Interphase', 'IVM':'Iiyama',
                              'LEN':'Lenovo', 'MAX':'Rogen Tech', 'MEG':'Abeam', 'MEI':'Panasonic',
                              'MTC':'Mars Tech', 'MTX':'Matrox', 'NEC':'NEC', 'ONK':'ONKYO', 'ORN':'Orion',
                              'OTM':'Opoma', 'OVR':'Oculus', 'PHL':'Philips', 'PIO':'Pioneer', 'PNR':'Planar',
                              'QDS':'Quanta', 'SAM':'Samsung','SEC':'Seiko','SHP':'Sharp','SII':'Silicon Image',
                              'SNY':'Sony', 'TOP':'Orion', 'TSB':'Toshiba', 'TST':'Transtream', 'UNK':'Unknown',
                              'VIZ':'Vizio', 'VSC':'ViewSonic', 'YMH':'Yamaha'}

    #checks the first 8 bits if they correspond to the pattern in EDID
    def checkdata(self):
        if not [x for x in self.edid[0:8]] == [0x00, 0xff, 0xff, 0xff, 0xff, 0xff,0xff, 0x00]:
            return False
        return True

    # byte 16 - number
    def getWeek(self):
        return self.edid[16] #byte 16

    # byte 17 - number
    def getYear(self):
        return self.edid[17] + 1990 #byte 17

    #Product ID, returns hex string
    def getManufactureCode(self):
        # convert byte 10-11 to hex string
        return binascii.hexlify(self.edid[11:12]).encode('utf8') + binascii.hexlify(self.edid[10:11]).encode('utf8')

    #Product ID, converts to Integer
    def getManufactureCode_num(self):
        hex_pc = self.getManufactureCode()
        return int((hex_pc), 16) #convert to 16-bit number

    #Serial Number, returns hex string
    def getSerialNumber(self):
        #convert byte 12-15 to hex string
        return binascii.hexlify(self.edid[15:16]).encode('utf8')+ binascii.hexlify(self.edid[14:15]).encode('utf8')+ binascii.hexlify(self.edid[13:14]).encode('utf8')+binascii.hexlify(self.edid[12:13]).encode('utf8')

    #Serial Number, returns integer
    def getSerialNumber_num(self):
        hex_sn = self.getSerialNumber()
        return int((hex_sn), 16) #convert to 32-bit number

    #Manufacture ID, bytes 8,9
    #byte 8, bits 6-2: First Letter
    #byte 8, bit 1 to byte 9 bit 5: Second Letter
    #byte 9, bits 4-0: Third Letter
    def getManufactureID(self):
        first = (self.edid[8] & 0b01111100) >> 2 #shifts right 2 bits - bits 6-2

        # get byte 8bits 1,0 and byte 9 bits 7-5
        second = ((self.edid[8] & 0b00000011) << 3)+ ((self.edid[9] & 0b11100000) >> 5)
        third = self.edid[9] & 0b00011111 # get bits 4-0

        return (chr(first + 64) + chr(second + 64) + chr(third + 64)) #Convert from PNP to ASCII '00001'=A, '11010'=Z

    #gets Manufacture Name with 3 letter ID
    def getManufactureName(self):
        code = self.getManufactureID()
        if self.manufacturers.get(code):
            return self.manufacturers.get(code)
        else:
            return "Can't get Manufacture name"

def main():
    print subprocess.call(['tvservice', '-d', 'EDID'])
    with open('EDID', 'rb') as f:
        i = 0
        edidstring = ""
        edid = bytearray()
        while (i < 128):
            raw = f.read(1)
            data = bytes(raw)  # byte

            hex_data = binascii.hexlify(data)
            text_string = hex_data.decode('utf-8')

            edidstring += text_string
            i = i + 1
            edid.append(data)

    p = EdidParser(edid)

    if p.checkdata():
        info = {'manufacture_id':p.getManufactureID(),'manufacture_name':p.getManufactureName(),'week':p.getWeek(),
                'year':p.getYear(), 'serial_number_hex':p.getSerialNumber(), 'serial_number_num':p.getSerialNumber_num(),
                'manufacture_code_hex':p.getManufactureCode(),'manufacture_code_num':p.getManufactureCode_num()}
        print 'week', p.getWeek(), 'of', p.getYear()
        print 'model:' + str(p.getManufactureCode())
        print 'serial Number:', str(p.getSerialNumber())
        print "Manufacture Id:", str(p.getManufactureID())
        print "Manufacture: ", p.getManufactureName()
        info['raw'] = edidstring
        jsonarray = json.dumps(info)
        print jsonarray
        return jsonarray
    else:
        print "not edid"
        return "not edid"




def connect():

    client_sock,address = server_sock.accept()
    print "Accepted connection from", address
    while True:
        try:
            data = client_sock.recv(1024)
            print "recieved [%s]" % data
            if (data == "exit"):
                print "Exit"
                break
            elif (data == "getedid"):
                print 'getting edid'
                client_sock.send(main())
        except (Exception):
            print "disconnected"
            client_sock.close()
            sleep(1000);
            connect()


server_sock = BluetoothSocket(RFCOMM)
port = server_sock.getsockname()[1]
server_sock.bind(("",port))
server_sock.listen(1)
uuid = "00001101-0000-1000-8000-00805F9B34FB"
advertise_service( server_sock, "EDIDPiServer",service_id = uuid,)
connect()


server_sock.close()
