cd build/docs/javadoc
git init
git remote add javadoc git@github.com:sargue/mailgun.git
git fetch --depth=1 javadoc gh-pages
git add --all
git commit -m "javadoc"
git merge --no-edit -s ours remotes/javadoc/gh-pages
git push javadoc master:gh-pages
rm -rf .git
