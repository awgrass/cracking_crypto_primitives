from math import floor
import sys

UTF_8_MAX = 0x10FFF

def encrypt(message, key):
    cipher = ''
    for letter in message:
        letter = pow(ord(letter), key[0], key[1])
        cipher = cipher + hex(letter) + ' '
    return cipher

def decrypt(cipher, key):
    message = ''
    for letter in cipher.split():
        num = pow(int(letter, 16), key[0], key[1])
        if num > UTF_8_MAX:
            return ""
        message = message + chr(num)
    return message

def get_nth_convergent(a_list):
  p_n1, q_n2 = 1, 1
  p_n2, q_n1 = 0, 0
  for i in range(len(a_list)):
    a_n = a_list[i]
    p_n = a_n * p_n1 + p_n2
    q_n = a_n * q_n1 + q_n2
    p_n2 = p_n1
    p_n1 = p_n
    q_n2 = q_n1
    q_n1 = q_n
    yield (p_n, q_n)



def fraction_expansion(nominator, denominator, max_recursion=1000):
  ret_val = []
  a_i = floor(nominator / denominator)
  ret_val.append(a_i)
  e_i = (nominator - (denominator * a_i), denominator)
  if e_i[0] == 0:
     return ret_val
  for _ in range(max_recursion):
    #this will be removed soon
    a_i = floor(e_i[1] / e_i[0])
    ret_val.append(a_i)
    e_i = (e_i[1] - (e_i[0] * a_i), e_i[0])
    #print("a_i is {} and epsilon {}".format(a_i, e_i))
    if e_i[0] == 0:
       break
  return ret_val

if __name__ == "__main__":
    argc = len(sys.argv)
    nominator = 0
    denominator = 0
    if  argc == 3:
        nominator = int(sys.argv[1])
        denominator = int(sys.argv[2])
    elif argc == 2:
        with open("pub_key.txt", "r") as f:
            nominator = int(f.readline(), 16)
            denominator = int(f.readline(), 16)
    else:
        print("usage: expansion.py [<nominator> <denominator>] | [file_path]")
        sys.exit()
    message = "THIS_IS_SO_SECRET"
    pub_key = (nominator, denominator)
    cipher = encrypt(message, pub_key)
    l = fraction_expansion(nominator, denominator)
    for x in get_nth_convergent(l):
        if decrypt(cipher, (x[1], denominator)) == message:
            print ("FOUND: " + str(x))
            sys.exit()
