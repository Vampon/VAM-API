import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import '@umijs/max';
const Footer: React.FC = () => {
  const defaultMessage = 'VAMPON出品';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        {
          key: 'VAM接口',
          title: 'VAM接口',
          href: 'https://pro.ant.design',
          blankTarget: true,
        },
        {
          key: 'github',
          title: <GithubOutlined />,
          href: 'https://github.com/Vampon',
          blankTarget: true,
        },
        {
          key: 'VAM接口',
          title: 'VAM接口',
          href: 'https://github.com/Vampon',
          blankTarget: true,
        },
      ]}
    />
  );
};
export default Footer;
