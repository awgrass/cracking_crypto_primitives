from hashlib import sha256
from sys import exit

from fractions import gcd

y = 10029
g = 2
p = 18461

"""y = 10
g = 5
p = 15"""

thresh1 = float(p)/3.0
thresh2 = float(2*p)/3.0

def egcd(a, b):
    if a == 0:
        return (b, 0, 1)
    else:
        g, y, x = egcd(b % a, a)
        return (g, x - (b // a) * y, y)

def modinv(a, m):
    g, x, y = egcd(a, m)
    return None if (g != 1) else (x % m)

def hash(input):
    #return sha256(str(input).encode("utf8")).hexdigest()[0:hash_length]
    ri = input[0]
    ai = input[1]
    bi = input[2]

    ri_next = 0
    ai_next = 0
    bi_next = 0
    if ri < thresh1:
        ri_next = (y * ri) % p
        ai_next = (ai + 1) % (p - 1)
        bi_next = bi
    elif ri < thresh2:
        ri_next = pow(ri, 2, p)
        ai_next = (2 * ai) % (p - 1)
        bi_next = (2 * bi) % (p - 1)
    elif ri < p:
        ri_next = (g * ri) % p
        ai_next = ai
        bi_next = (bi + 1) % (p - 1)
    else:
        print("we fucked up")
        sys.exit()
    return (ri_next, ai_next, bi_next)

def find_cycle():
    rj, aj, bj = hash((1, 0, 0))
    rk, ak, bk = hash((rj, aj, bj))

    while rj != rk:
        rj, aj, bj = hash((rj, aj, bj))
        rk, ak, bk = hash(hash((rk, ak, bk)))
    return (aj, bj), (ak, bk)

def find_x(aj, ak, bj, bk):
    new_aj_ak = -(aj - ak)
    new_bk_bj = -(bk - bj)
    modulus = p - 1
    inv = modinv(new_aj_ak, modulus)
    while inv is None:
        divisor = gcd(new_aj_ak, modulus)
        new_aj_ak /= divisor
        new_bk_bj /= divisor
        modulus /= divisor
        inv = modinv(new_aj_ak, modulus)
    x = ((new_bk_bj) * inv) % (modulus)
    i = 0
    while True: 
        if pow(g, x + (i*modulus), p) == y:
            print("FOUND!")
            print(x + (i*p))
            break
        i = i + 1



if __name__ == "__main__":
    j, k = find_cycle()
    find_x(j[0], k[0], j[1], k[1])
