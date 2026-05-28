import re
import os

root = os.path.dirname(os.path.dirname(__file__))
java_root = os.path.join(root, 'src', 'main', 'java')
res_items = os.path.join(root, 'src', 'main', 'resources', 'assets', 'hbm', 'textures', 'items')

pattern = re.compile(r'setTextureName\([^\)]*:\\"?([^\)\"\']+)\"?\)')
pattern2 = re.compile(r'setTextureName\(RefStrings\.MODID \+ \":([^\"]+)\"')
pattern3 = re.compile(r'setTextureName\(\"([^\"]+):([^\"]+)\"\)')

refs = set()
for dirpath, dirnames, filenames in os.walk(java_root):
    for fn in filenames:
        if fn.endswith('.java'):
            path = os.path.join(dirpath, fn)
            try:
                s = open(path, encoding='utf-8').read()
            except:
                continue
            for m in pattern2.finditer(s):
                refs.add(m.group(1))
            # generic search for :name inside setTextureName
            for m in re.finditer(r'setTextureName\([^;\n\r]*\)', s):
                call = m.group(0)
                # extract between quotes
                q = re.search(r'"([^":]+):([^\"]+)"', call)
                if q:
                    refs.add(q.group(2))
                else:
                    # fallback: find colon and following chars
                    mm = re.search(r':([a-zA-Z0-9_\./]+)\b', call)
                    if mm:
                        name = mm.group(1)
                        if '/' in name:
                            # e.g. forgefluid/water -> skip (not in items)
                            pass
                        else:
                            refs.add(name)

# also detect textures set via setTextureName(RefStrings.MODID + ":ammo_standard") style already handled

# list files in res_items
files = set()
for fn in os.listdir(res_items):
    if fn.endswith('.png'):
        files.add(fn[:-4])

missing = sorted([r for r in refs if r not in files])

print('Found {} texture refs in code, {} item texture files.'.format(len(refs), len(files)))
print('Missing textures (count={}):'.format(len(missing)))
for m in missing:
    print(m)

# write to output file for later consumption
out = os.path.join(root, 'tools', 'missing_textures.txt')
with open(out, 'w', encoding='utf-8') as f:
    for m in missing:
        f.write(m + '\n')
print('\nWrote ' + out)