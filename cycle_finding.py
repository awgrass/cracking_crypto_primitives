from hashlib import sha256
from sys import exit
from fractions import gcd

y = 10029
g = 2
p = 18461

#y = 16
#g = 5
#p = 23

thresh1 = float(p)/3.0
thresh2 = float(2*p)/3.0

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

    print ("rj {} aj {} bj {}".format(rj, aj, bj))
    print ("rk {} ak {} bk {}".format(rk, ak, bk))

    while rj != rk:
        rj, aj, bj = hash((rj, aj, bj))
        rk, ak, bk = hash(hash((rk, ak, bk)))
    return (aj, bj), (ak, bk)

def find_x(aj, ak, bj, bk):

    if (y == (g**x) % p):
        print("FOUND!")
        print(x)



if __name__ == "__main__":
    j, k = find_cycle()
    print ("{} {}".format(j,k))
    find_x(j[0], k[0], j[1], k[1])
