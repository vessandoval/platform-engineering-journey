#!/usr/bin/env bash

# Laboratorio 01 - Linux para Platform Engineers
# Inventario inicial y diagnóstico del servidor RHEL

set -u

FECHA=$(date '+%Y%m%d_%H%M%S')
HOST=$(hostname -s 2>/dev/null || echo "servidor")
ARCHIVO="diagnostico_${HOST}_${FECHA}.txt"

# Capturar stdout y stderr de todo el script
exec > >(tee "$ARCHIVO") 2>&1

seccion() {
    printf '\n'
    printf '%s\n' "=================================================="
    printf ' %s\n' "$1"
    printf '%s\n' "=================================================="
}

ejecutar() {
    printf '\n$'
    printf ' %q' "$@"
    printf '\n'
    "$@"
    RC=$?
    printf '[Código de salida: %s]\n' "$RC"
    return 0
}

seccion "LABORATORIO 01 - INVENTARIO RHEL"
printf 'Fecha: %s\n' "$(date '+%F %T %Z')"
printf 'Host: %s\n' "$HOST"

seccion "1. VERSION DE RHEL"
ejecutar cat /etc/redhat-release

seccion "2. IDENTIDAD Y CONFIGURACION DEL SISTEMA"
ejecutar hostnamectl

seccion "3. TIEMPO DE ACTIVIDAD Y CARGA"
ejecutar uptime

seccion "4. USUARIO ACTUAL"
ejecutar whoami
ejecutar id

seccion "5. ESPACIO Y TIPOS DE FILESYSTEM"
ejecutar df -hT

seccion "6. USO DE INODOS"
ejecutar df -ih

seccion "7. MEMORIA"
ejecutar free -h

seccion "8. DISCOS, PARTICIONES Y FILESYSTEMS"
ejecutar lsblk -f

seccion "9. DIRECCIONES IP"
ejecutar ip -br address

seccion "10. TABLA DE RUTAS"
ejecutar ip route

seccion "11. SERVICIOS FALLIDOS"
ejecutar systemctl --failed

seccion "FIN DEL DIAGNOSTICO"
printf 'Archivo generado: %s/%s\n' "$PWD" "$ARCHIVO"
