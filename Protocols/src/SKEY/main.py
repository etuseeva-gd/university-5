from hashlib import md5
import sys, os


def register():
    name = input("Введите ваше имя: ")
    pwd = name + ".txt"
    if os.path.exists(pwd):
        print("Ошибка. такой пользователь уже существует")
        sys.exit(-1)
    r = input("Введите ваше секретное число:")
    new_name = name + "-keys.txt"
    d = open(new_name, "w")
    for i in range(100):
        r = md5(r.encode("utf-8")).hexdigest()
        k = str(i) + " " + r
        d.write("%s\n" % (k))
    d.close()
    key = md5(r.encode("utf-8")).hexdigest()
    try:
        f = open(pwd, "w")
        print("Ваши ключи сгенерированны")
    except:
        print("Ошибка")
        sys.exit(-1)
    f.write(key)
    f.close()


def login():
    name = input("Введите ваше имя: ") + ".txt"
    try:
        f = open(name, "r")
    except:
        print("Ошибка")
        sys.exit(-1)
    passwd = f.read()
    f.close()
    upasswd = input("Введите пароль: ")
    new_upasswd = md5(upasswd.encode("utf-8")).hexdigest()
    if new_upasswd != passwd:
        print("Неверный пароль!")
    else:
        print("Успешная авторизация!")
        f = open(name, "w")
        f.write(upasswd)
        f.close()


if __name__ == "__main__":
    while True:
        action = input("Выберите действие:\n1 - регистрация\n2 - авторизация\n3 - выход\n")
        if action == "1":
            register()
        elif action == "2":
            login()
        elif action == "3":
            sys.exit(0)
