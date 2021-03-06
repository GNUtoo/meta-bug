# Copyright Matthias Hentges <devel@hentges.net> (c) 2007
# License: MIT (see http://www.opensource.org/licenses/mit-license.php
#               for a copy of the license)
#
# Filename: alsa-state.bb

DESCRIPTION = "Alsa Scenario Files"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=3f40d7994397109285ec7b81fdeb3b58"
PV = "0.2.0"
PR = "r0"

SRC_URI = "\
  file://asound.conf \
  file://asound.state \
  file://alsa-state \
"

inherit update-rc.d

INITSCRIPT_NAME = "alsa-state"
INITSCRIPT_PARAMS = "start 39 S . stop 31 0 6 ."

do_install() {
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/alsa-state ${D}${sysconfdir}/init.d

    install -m 0644 ${WORKDIR}/asound.conf ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/*.state ${D}${sysconfdir}
}

PACKAGES += "alsa-states"

RRECOMMENDS_alsa-state = "alsa-states"

FILES_${PN} = "${sysconfdir}/init.d ${sysconfdir}/asound.conf"
CONFFILES_${PN} = "${sysconfdir}/asound.conf"

FILES_alsa-states = "${sysconfdir}/*.state"

PACKAGE_ARCH = "all"
PACKAGE_ARCH_alsa-states = "${MACHINE_ARCH}"

pkg_postinst_${PN}() {
	if test -z "$D"
	then
		if test -x /usr/sbin/alsactl
		then
			/usr/sbin/alsactl -f ${sysconfdir}/asound.state restore
		fi
		# INITSCRIPT_PARAMS changed, so remove the old and
		# install the new setting.
		update-rc.d -f ${INITSCRIPT_NAME} remove
		update-rc.d ${INITSCRIPT_NAME} ${INITSCRIPT_PARAMS}
	fi
}
