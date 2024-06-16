import myaa.subkt.ass.*
import myaa.subkt.tasks.*
import myaa.subkt.tasks.Mux.*
import myaa.subkt.tasks.Nyaa.*
import java.awt.Color
import java.time.*

plugins {
    id("myaa.subkt")
}

subs {
    readProperties("sub.properties")
    episodes(getList("episodes"))
    batches(getMap("batches", "episodes"))

    merge {
        from(get("scripts"))

        if (propertyExists("opsync")) {
            fromIfPresent(get("OP")) {
                syncTargetTime(getAs<Duration>("opsync"))
            }
        }

        if (propertyExists("edsync")) {
            fromIfPresent(get("ED")) {
                syncTargetTime(getAs<Duration>("edsync"))
            }
        }

        if (propertyExists("edsync2")) {
            fromIfPresent(get("ED2")) {
                syncTargetTime(getAs<Duration>("edsync2"))
            }
        }
    }

    chapters {
        from(get("scripts"))
        chapterMarker("chapter")
        generateIntro(false)
    }

    mux {
        title(get("showtitle"))
        onMissingGlyphs(ErrorMode.WARN)
        onMissingFonts(ErrorMode.WARN)

        from(get("premux")) {
            tracks {
                include(track.type == TrackType.AUDIO || track.type == TrackType.VIDEO)
                lang("jpn")
            }
        }

        from(merge.item()) {
            tracks {
                name("Haruzora-N4O")
                lang("id")
                default(true)
            }
        }

        chapters(chapters.item()) {
            lang("id")
            charset("UTF-8")
        }

        attach(get("fonts")) {
            includeExtensions("ttf", "otf")
        }

        attach(get("songfonts")) {
            includeExtensions("ttf", "otf")
        }

        out(get("muxfile"))
    }

    torrent {
        from(mux.item())
        trackers(getList("trackers"))
        out(get("torrentfile"))
    }

    alltasks {
        torrent {
            trackers(getList("trackers"))

            from(mux.batchItems()) {
                if (isBatch) {
                    into(get("batchdir"))
                }
            }

            out(get("torrentfile"))
        }
    }
}
