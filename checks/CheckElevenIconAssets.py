#!/usr/bin/env python3
"""Verify that Android receives the documented lossless icon exports."""
from pathlib import Path
import hashlib
import json
import struct

root = Path(__file__).resolve().parents[1]
manifest = json.loads((root / 'docs/ELEVEN_ICON_ASSETS.json').read_text())
seen = set()
for entry in manifest['icons']:
    name = entry['resource']
    assert name not in seen, f'Duplicate resource: {name}'
    seen.add(name)
    raw = (root / 'app/src/main/res/drawable-nodpi' / (name + '.png')).read_bytes()
    assert hashlib.sha256(raw).hexdigest() == entry['png_sha256'], f'Icon changed: {name}'
    assert raw[:8] == b'\x89PNG\r\n\x1a\n' and raw[12:16] == b'IHDR', name
    width, height, depth, color, *_ = struct.unpack('>IIBBBBB', raw[16:29])
    assert [width, height] == entry['selected_frame'], f'Wrong frame size: {name}'
    assert [width, height] == max(entry['available_frames'], key=lambda size: size[0] * size[1]), name
    assert depth == 8 and color == 6, f'Transparent RGBA source required: {name}'
    assert entry['pixels_preserved'] is True, name
assert len(seen) == 46
print(f'PASS: {len(seen)} pinned source icon exports, checksums, largest frames and RGBA transparency')
