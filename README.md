mp3やwavのID3タグを読む、簡易的な自作ライブラリです。

SoundTagSystem.getTagsを実行すれば取れます。
id3v2.3とid3v1、v1.1のタグしか取れません。v2.4は全く対応していないです。

大体読めると思いますが、だめならID3V23TagKeysやRIFFTagKeysに処理を追加してください。

wavの場合、RIFF（と、その中に入ってるID3v2.3）をとれますが、RIFF領域の日本語は文字化けします←面倒なので処理してない
RIFFタグの中に、"LIST....INFO"　（.は任意の1バイト）の文字列があると、解析失敗します。コメントとかタイトルで使わないでください。

RIFFもID3も、Mp3tag ( https://www.mp3tag.de/en/ )で作ったやつなら読めるはずです。

