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
      // copyright={`${currentYear} ${defaultMessage}`}
      copyright={<>
        {`${currentYear} ${defaultMessage}`} | {' '}
        <a target={'_blank'} href={"https://vampon.github.io/VAM-API-DOC/pages/745wda/"} rel="noreferrer"> 免责声明 </a>
        |
        <a target={'_blank'} href={"https://beian.miit.gov.cn/"} rel="noreferrer"> 豫ICP备2024079582号 </a>
      </>}
      links={[
        {
          key: 'Ant Design Pro',
          title: 'Ant Design Pro',
          href: 'https://pro.ant.design',
          blankTarget: true,
        },
        // {
        //   key: 'github',
        //   title: <GithubOutlined />,
        //   href: 'https://github.com/Vampon',
        //   blankTarget: true,
        // },
        // {
        //   key: 'VAM接口',
        //   title: 'VAM接口',
        //   href: 'https://github.com/Vampon',
        //   blankTarget: true,
        // },
      ]}
    />
  );
};
export default Footer;
